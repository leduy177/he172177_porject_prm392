using Google.Cloud.Firestore;
using PRM_Project.Models;

namespace PRM_Project.Services
{
    public class FirestoreService
    {
        private readonly FirestoreDb _db;

        public FirestoreService(IConfiguration configuration)
        {
            string relativePath = configuration["Firestore:ServiceAccountPath"];
            string pathToKey = Path.Combine(Directory.GetCurrentDirectory(), relativePath);
            string projectId = configuration["Firestore:ProjectId"];

            if (!File.Exists(pathToKey))
            {
                throw new FileNotFoundException($"Không tìm thấy file key: {pathToKey}");
            }

            Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", pathToKey);
            _db = FirestoreDb.Create(projectId);
        }

        //cart
        public async Task<List<CartModel>> GetCartAsync()
        {
            List<CartModel> result = new();
            CollectionReference cartRef = _db.Collection("cart");
            QuerySnapshot snapshot = await cartRef.GetSnapshotAsync();

            foreach (var doc in snapshot.Documents)
            {
                var data = doc.ToDictionary();
                var cartItem = new CartModel
                {
                    Id = doc.Id,
                    Name = data.ContainsKey("name") ? data["name"]?.ToString() : "",
                    Image = data.ContainsKey("image") ? data["image"]?.ToString() : "",
                    Price = data.ContainsKey("price") ? Convert.ToInt32(data["price"]) : 0,
                    Quantity = data.ContainsKey("quantity") ? Convert.ToInt32(data["quantity"]) : 0,
                    UserId = data.ContainsKey("userId") ? data["userId"]?.ToString() : ""
                };
                result.Add(cartItem);
            }

            return result;
        }

        // READ ALL
        public async Task<List<ProductModel>> GetAllProductsAsync()
        {
            List<ProductModel> products = new();
            CollectionReference productsRef = _db.Collection("products");
            QuerySnapshot snapshot = await productsRef.GetSnapshotAsync();

            foreach (var doc in snapshot.Documents)
            {
                var data = doc.ToDictionary();

                var product = new ProductModel
                {
                    Id = doc.Id,
                    Name = data.ContainsKey("name") ? data["name"].ToString() : "",
                    Description = data.ContainsKey("description") ? data["description"].ToString() : "",
                    Image = data.ContainsKey("image") ? data["image"].ToString() : "",
                    Price = data.ContainsKey("price") ? Convert.ToInt32(data["price"]) : 0,
                    Quantity = data.ContainsKey("quantity") ? Convert.ToInt32(data["quantity"]) : 0,
                    Category = data.ContainsKey("category") ? Convert.ToInt32(data["category"]) : 0
                };

                products.Add(product);
            }

            return products;
        }

        // READ BY ID
        public async Task<ProductModel?> GetProductByIdAsync(string id)
        {
            var docRef = _db.Collection("products").Document(id);
            var snapshot = await docRef.GetSnapshotAsync();
            if (!snapshot.Exists)
                return null;

            var data = snapshot.ToDictionary();
            return new ProductModel
            {
                Id = snapshot.Id,
                Name = data.GetValueOrDefault("name")?.ToString() ?? "",
                Description = data.GetValueOrDefault("description")?.ToString() ?? "",
                Image = data.GetValueOrDefault("image")?.ToString() ?? "",
                Price = data.GetValueOrDefault("price") != null ? Convert.ToInt32(data["price"]) : 0,
                Quantity = data.GetValueOrDefault("quantity") != null ? Convert.ToInt32(data["quantity"]) : 0,
                Category = data.GetValueOrDefault("category") != null ? Convert.ToInt32(data["category"]) : 0
            };
        }

        // CREATE
        public async Task<string> AddProductAsync(ProductModel product)
        {
            var dict = new Dictionary<string, object>
    {
        { "name", product.Name },
        { "description", product.Description },
        { "image", product.Image },
        { "price", product.Price },
        { "quantity", product.Quantity },
        { "category", product.Category }
    };

            var docRef = _db.Collection("products").Document(product.Id);
            await docRef.SetAsync(dict);
            return product.Id;

        }


        // UPDATE
        public async Task UpdateProductAsync(ProductModel product)
        {
            var docRef = _db.Collection("products").Document(product.Id);
            var dict = new Dictionary<string, object>
            {
                { "name", product.Name },
                { "description", product.Description },
                { "image", product.Image },
                { "price", product.Price },
                { "quantity", product.Quantity },
                { "category", product.Category }
            };

            await docRef.SetAsync(dict, SetOptions.Overwrite);
        }

        // DELETE
        public async Task DeleteProductAsync(string id)
        {
            var docRef = _db.Collection("products").Document(id);
            await docRef.DeleteAsync();
        }

       


    }
}
