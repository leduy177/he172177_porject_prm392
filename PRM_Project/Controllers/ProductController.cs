using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using PRM_Project.Models;
using PRM_Project.Services;


namespace PRM_Project.Controllers
{
    public class ProductController : Controller
    {
        private readonly FirestoreService _firestoreService;
        public ProductController(FirestoreService firestoreService)
        {
            _firestoreService = firestoreService;
        }



        public async Task<IActionResult> Index()
        {
            var products = await _firestoreService.GetAllProductsAsync();
            return View(products);
        }
        [HttpGet]
        public async Task<IActionResult> Search(string? searchString)
        {
            var products = await _firestoreService.GetAllProductsAsync();

            if (!string.IsNullOrEmpty(searchString))
            {
                searchString = searchString.ToLower();
                products = products.Where(p =>
                    (!string.IsNullOrEmpty(p.Name) && p.Name.ToLower().Contains(searchString)) ||
                    (!string.IsNullOrEmpty(p.Description) && p.Description.ToLower().Contains(searchString))
                ).ToList();
            }

            return Json(products);
        }

        // GET CREATE
        public IActionResult Create()
        {
            ViewBag.CategoryList = GetCategoryList();
            return View();
        }

        // POST CREATE
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Create(ProductModel product)
        {
            if (string.IsNullOrEmpty(product.Id))
            {
                product.Id = Guid.NewGuid().ToString();
            }

            if (ModelState.IsValid)
            {
                try
                {
                    await _firestoreService.AddProductAsync(product);
                    return RedirectToAction(nameof(Index));
                }
                catch (Exception ex)
                {
                    ModelState.AddModelError(string.Empty, $"Lỗi: {ex.Message}");
                }
            }

            return View(product);
        }



        // GET EDIT
        public async Task<IActionResult> Edit(string id)
        {
            var product = await _firestoreService.GetProductByIdAsync(id);
            if (product == null)
                return NotFound();
            ViewBag.CategoryList = GetCategoryList();
            return View(product);
        }

        // POST EDIT
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(string id, ProductModel product)
        {
            if (id != product.Id)
                return BadRequest();

            if (ModelState.IsValid)
            {
                await _firestoreService.UpdateProductAsync(product);
                return RedirectToAction(nameof(Index));
            }
            return View(product);
        }

        // GET DELETE (confirm page)
        public async Task<IActionResult> Delete(string id)
        {
            var product = await _firestoreService.GetProductByIdAsync(id);
            if (product == null)
                return NotFound();

            return View(product);
        }

        // POST DELETE
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> DeleteConfirmed(string id)
        {
            await _firestoreService.DeleteProductAsync(id);
            return RedirectToAction(nameof(Index));
        }

        // GET DETAILS
        public async Task<IActionResult> Details(string id)
        {
            var product = await _firestoreService.GetProductByIdAsync(id);
            if (product == null)
                return NotFound();

            return View(product);
        }
    

   
private List<SelectListItem> GetCategoryList()
        {
            return new List<SelectListItem>
    {
        new SelectListItem { Value = "1", Text = "Tăm hương" },
        new SelectListItem { Value = "2", Text = "Nhang" },
        new SelectListItem { Value = "3", Text = "Vòng Tay" },
        new SelectListItem { Value = "4", Text = "Đồ Decor" },
    };
        }
    }
}
