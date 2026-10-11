import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-product',
  templateUrl: './createproduct.component.html',
  styleUrls: ['./createproduct.component.css']
})
export class CreateproductComponent {
  product = {
    productName: '',
    categoryId: '',
    productDetails: '',
    actualPrice: 0,
    offerPercentage: 0,
    productImageUrl: '',
    usageRewardPoints: 0
  };

  categories = ['Electronic', 'Mobiles', 'Watches', 'Cloths', 'Fashion', 'Health'];
  imagePreview: string | ArrayBuffer | null = null;

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('User not authenticated. Please login first.');
      return;
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    this.http.post('http://localhost:8080/product/v1', this.product, { headers }).subscribe({
      next: () => alert('✅ Product created successfully!'),
      error: (err) => {
        console.error('❌ Failed:', err);
        alert('Failed to create product.');
      }
    });
    this.router.navigate(['/home']);
  }

  onImageSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result;
      };
      reader.readAsDataURL(file);

      this.product.productImageUrl = 'assets/' + file.name;
    }
  }
  logout(): void {
    this.router.navigate(['/login']);
  }
}
