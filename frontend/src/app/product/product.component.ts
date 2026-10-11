import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.css']
})
export class ProductComponent implements OnInit {
  products: any[] = [];
  productIDs: any[] = [];
  user: any;
  walletInfo: any = null;
  isAdmin: boolean = false;

  constructor(private http: HttpClient, private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    const role = localStorage.getItem('role');
    this.isAdmin = role === 'ROLE_ADMIN';
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');

    if (!token || !userName) {
      console.error('Missing token or username');
      return;
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    this.loadWalletInfo();

    console.log("userName = " + userName);

    // Fetch user
    this.http.get<any>(`http://localhost:8080/user/v1?userName=${userName}`, { headers })
      .subscribe({
        next: data => {
          if (data && data.data && data.data.length > 0) {
            this.user = data.data[0];
          }
        },
        error: err => console.error('User fetch failed:', err)
      });


    // Fetch products
    this.http.get<any>(`http://localhost:8080/product/v1/all?mode=NONE`, { headers })
      .subscribe({
        next: data => {
          if (data && data.data && data.data.length > 0) {
            const rawProducts = data.data[0];
            this.products = rawProducts.map((product: any) => ({
              ...product,
              discountPrice: this.calculateDiscount(product.actualPrice, product.offerPercentage)
            }));
          }
        },
        error: err => console.error('Product fetch error:', err)
      });
  }

  private calculateDiscount(price: number, offer: number): number {
    return price - (price * offer) / 100;
  }

  logout(): void {
    this.router.navigate(['/login']);
  }

  addToKart(productId: string): void {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('username') ?? '';
    console.log("Method Calling " + productId);

    if (!token || !userId) {
      alert('User not logged in');
      return;
    }



    // Build request payload
    const payload = {
      userId: userId,
      products: [productId]
    };

    console.log("pauload ..... "+payload)

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    this.http.put('http://localhost:8080/kart/v1/addproducts', payload, { headers })
      .subscribe({
        next: (res) => {
          alert('Cart updated');
          this.router.navigate(['/products']);
        },
        error: (err) => {
          console.error('Error:', err);
          alert('Failed to add to cart');
        }
      });
  }

  loadWalletInfo() {

    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('username');

    if (!token || !userId) {
      alert('User not logged in');
      return;
    }
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
    console.log("Loading wallet info......")

    this.http.get<any>(
      `http://localhost:8080/wallet/v1?userId=${userId}`,
      { headers }
    ).subscribe({
      next: res => {
        this.walletInfo = res?.data?.[0]; // Get first wallet entry from array
        console.log("✅ Wallet Info:", this.walletInfo);
      },
      error: err => {
        console.error('❌ Failed to load wallet info:', err);
      }
    });
  }

  gotoKart(): void {
    const role = localStorage.getItem('role');
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
    localStorage.setItem('role', role);
    this.router.navigate(['/kart']);

  }

  viewOrderHistory(): void {
    const role = localStorage.getItem('role');
  localStorage.setItem('role', role);
  const token = localStorage.getItem('token');
  localStorage.setItem('jwt', token ?? '');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
  this.router.navigate(['/order']);
}

  addMoney(): void {
  const token = localStorage.getItem('token');
  const userId = localStorage.getItem('username') ?? '';

  if (!token || !userId) {
    alert('User not logged in');
    return;
  }

  const amountStr = prompt('Enter amount to add to wallet:');
  const amount = parseFloat(amountStr || '');

  if (isNaN(amount) || amount <= 0) {
    alert('Please enter a valid amount.');
    return;
  }

  const payload = {
    userId: userId,
    amount: amount
  };

  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  });

  this.http.put('http://localhost:8080/wallet/v1/addmoney', payload, { headers })
    .subscribe({
      next: (res) => {
        alert('Money added successfully to wallet!');
        this.loadWalletInfo(); // refresh wallet info
      },
      error: (err) => {
        console.error('Error adding money:', err);
        alert('Failed to add money.');
      }
    });
}

goToPayment(): void{
  const role = localStorage.getItem('role');
  localStorage.setItem('role', role);
  const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
  this.router.navigate(['/payment']);

}

addNewProduct(): void{

  this.router.navigate(['/createproduct']);

}

showProductDetails(product: any): void {
  const message = `
🛍️ Product: ${product.productName}
🆔 ID: ${product.productId}
📦 Category: ${product.categoryId}
📝 Details: ${product.productDetails}
💰 Actual Price: ₹${product.actualPrice}
🏷️ Discount: ${product.offerPercentage}%
🎁 Reward Points: ${product.usageRewardPoints}
📸 Image URL: ${product.productImageUrl}
  `;
  alert(message);
}

onDeleteProduct(productId: string): void {
  const confirmed = confirm('Are you sure you want to delete this product?');
  if (!confirmed) return;

  const token = localStorage.getItem('token');

  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  });

  this.http.delete(`http://localhost:8080/product/v1?productId=${productId}`, { headers })
    .subscribe({
      next: () => {
        alert('Product deleted successfully.');
        this.products = this.products.filter(p => p.productId !== productId); // remove from UI
      },
      error: (err) => {
        console.error('Delete failed:', err);
        alert('Failed to delete product.');
      }
    });
}

onUpdateProduct(product: any): void {
  this.router.navigate(['/updateproduct'], { state: { product } });
}

goToAdminPage(){
  const role = localStorage.getItem('role');
  localStorage.setItem('role', role);
  const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
  this.router.navigate(['/home']);
}

myProfile(){
  const role = localStorage.getItem('role');
  localStorage.setItem('role', role);
  const token = localStorage.getItem('token');
  const userName = localStorage.getItem('username');
  localStorage.setItem('userName', userName ?? '');
  localStorage.setItem('jwt', token ?? '');
  this.router.navigate(['/userhome']);

}


searchText: string = '';

filteredProducts() {
  return this.products.filter(product =>
    product.productName.toLowerCase().includes(this.searchText.toLowerCase())
  );
}

showModal: boolean = false;

showImagePreview() {
  this.showModal = true;
}

closeImagePreview() {
  this.showModal = false;
}

sortField: string = '';
sortDirection: 'asc' | 'desc' = 'asc';

sortProducts(field: string): void {
  if (this.sortField === field) {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
  } else {
    this.sortField = field;
    this.sortDirection = 'asc';
  }

  this.products.sort((a, b) => {
    const valA = a[field];
    const valB = b[field];

    if (valA < valB) return this.sortDirection === 'asc' ? -1 : 1;
    if (valA > valB) return this.sortDirection === 'asc' ? 1 : -1;
    return 0;
  });
}

menuOpen: boolean = false;

toggleMenu() {
  this.menuOpen = !this.menuOpen;
}





}