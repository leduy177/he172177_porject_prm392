using Microsoft.AspNetCore.Mvc.ModelBinding;

namespace PRM_Project.Models
{
    public class ProductModel
    {
 
        public string Id { get; set; } = "";
        public string Name { get; set; }
        public string Description { get; set; }
        public string Image { get; set; }
        public int Price { get; set; }
        public int Quantity { get; set; }
        public int Category { get; set; }
    }
}
