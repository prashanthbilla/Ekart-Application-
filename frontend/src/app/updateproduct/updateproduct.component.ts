import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'updateproduct',
  templateUrl: './updateproduct.component.html',
  styleUrl: './updateproduct.component.css'
})
export class UpdateproductComponent implements OnInit {
  product: any = {};
  error: string = '';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    const productData = history.state?.product;
    if (productData) {
      this.product = { ...productData };
    } else {
      this.error = 'No product data found.';
    }
  }

  updateProduct() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    this.http.put('http://localhost:8080/product/v1', this.product, { headers })
      .subscribe({
        next: () => {
          alert('Product updated successfully!');
          this.router.navigate(['/products']);
        },
        error: (err) => {
          console.error(err);
          this.error = 'Failed to update product.';
        }
      });
  }

  onImagePicked(event: Event): void {
  const fileInput = event.target as HTMLInputElement;

  if (fileInput.files && fileInput.files[0]) {
    const file = fileInput.files[0];
    const fileName = file.name; // Extract only the file name

    // Assume image is stored in: assets/products/<filename>
    this.product.productImageUrl = 'assets/' + fileName;
  }
}


  logout(): void {
    this.router.navigate(['/login']);
  }
}
