using Microsoft.AspNetCore.Mvc;
using PRM_Project.Models;
using PRM_Project.Services;
namespace PRM_Project.Controllers
{
    public class CartController : Controller
    {
        private readonly FirestoreService _firestoreService;
        public CartController(FirestoreService firestoreService)
        {
            _firestoreService = firestoreService;
        }
        public async Task<IActionResult> Index()
        {
            var p = await _firestoreService.GetCartAsync();
            return View(p);
        }
    
        [HttpGet]
        public async Task<IActionResult> Search(string? searchString)
        {
            var products = await _firestoreService.GetCartAsync();

            if (!string.IsNullOrEmpty(searchString))
            {
                searchString = searchString.ToLower();
                products = products.Where(p =>
                    (!string.IsNullOrEmpty(p.Name) && p.Name.ToLower().Contains(searchString)) ||
                    (!string.IsNullOrEmpty(p.UserId) && p.UserId.ToLower().Contains(searchString))
                ).ToList();
            }

            return Json(products);
        }
    } }
