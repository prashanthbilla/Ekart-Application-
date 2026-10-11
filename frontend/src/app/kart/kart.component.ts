import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-kart',
  templateUrl: './kart.component.html',
  styleUrls: ['./kart.component.css']
})
export class KartComponent implements OnInit {
  userName: string = '';
  kartInfo: any = null;
  kartData: any = null;
  products: any[] = [];
  walletInfo: any = null;
  selectedProductIds: Set<string> = new Set();
  paymentMode: string = 'WALLET';

  constructor(private http: HttpClient, private router: Router) { }
  

  ngOnInit(): void {
    const storedUser = localStorage.getItem('userName');
    const token = localStorage.getItem('jwt');
    console.log("userName in Kart:", storedUser);
    console.log("Token in Kart:", token);

    if (!storedUser || !token) {
      alert('User not logged in. Redirecting to login.');
      this.router.navigate(['/login']);
      return;
    }

    this.userName = storedUser;
    this.loadKartInfo();
    this.loadWalletInfo();
  }

  getJwtHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt') || '';
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  loadKartInfo() {
    const headers = this.getJwtHeaders();

    this.http.get<any>(`http://localhost:8080/kart/v1?userId=${this.userName}`, { headers }).subscribe({
      next: (res) => {
        console.log("Kart Response:", res);
        this.kartInfo = res;
        this.kartData = res?.data?.[0];

        if (this.kartData?.products) {
          const productIds = Object.keys(this.kartData.products);
          this.fetchProductsByIds(productIds);
        }
      },
      error: err => {
        console.error('Failed to load kart info:', err);
        alert('Could not load kart. Please login again.');
        this.router.navigate(['/login']);
      }
    });
  }

  fetchProductsByIds(ids: string[]) {
    this.products = [];
    const tempProducts: any[] = [];
    const headers = this.getJwtHeaders();

    ids.forEach(productId => {
      const quantity = this.kartData.products[productId]; // 👈 get quantity from backend
      this.http.get<any>(`http://localhost:8080/product/v1?productId=${productId}`, { headers }).subscribe({
        next: res => {
          const product = res?.data?.[0];
          if (product) {
            product.quantity = quantity; // 👈 attach quantity to each product
            tempProducts.push(product);
          }
          if (tempProducts.length === ids.length) {
            this.products = tempProducts;
            console.log('✅ Final Product List:', this.products);
          }
        },
        error: err => console.error(`❌ Failed to load product ${productId}:`, err)
      });
    });
  }


  toggleProductSelection(productId: string, checked: boolean): void {
    if (checked) {
      this.selectedProductIds.add(productId);
    } else {
      this.selectedProductIds.delete(productId);
    }
  }

  selectAll(event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked) {
      this.products.forEach(p => this.selectedProductIds.add(p.productId));
    } else {
      this.selectedProductIds.clear();
    }
  }

  get selectedProducts() {
    return this.products.filter(p => this.selectedProductIds.has(p.productId));
  }

  get totalOriginalPrice() {
  return this.selectedProducts.reduce((sum, p) => {
    const qty = this.kartData?.products?.[p.productId] || 1;
    return sum + p.actualPrice * qty;
  }, 0);
}


  get totalDiscountPrice() {
  return this.selectedProducts.reduce((sum, p) => {
    const qty = this.kartData?.products?.[p.productId] || 1;
    const discount = p.actualPrice * (p.offerPercentage / 100);
    const discountedPrice = p.actualPrice - discount;
    return sum + discountedPrice * qty;
  }, 0);
}


  loadWalletInfo() {
    const headers = this.getJwtHeaders();
    console.log("Loading wallet info......")

    this.http.get<any>(
      `http://localhost:8080/wallet/v1?userId=${this.userName}`,
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


  placeOrder() {
    const headers = this.getJwtHeaders();
    const isAllSelected = this.selectedProductIds.size === this.products.length;

    let endpoint = '';
    let orderPayload: any;

    if (isAllSelected) {
      console.log("All");
      endpoint = 'http://localhost:8080/payment/v1/products';
      orderPayload = {
        userId: this.userName,
        paymentMode: this.paymentMode
      };
      console.log(orderPayload)
    } else {
      endpoint = 'http://localhost:8080/payment/v1/buysomeproducts';
      orderPayload = {
        userId: this.userName,
        paymentMode: this.paymentMode.toString().toUpperCase,
        products: Array.from(this.selectedProductIds)
      };
    }

    this.http.put(endpoint, orderPayload, { headers }).subscribe({
      next: () => {
        alert('Order placed successfully!');
        this.router.navigate(['/ordersuccess']);
      },
      error: err => {
        console.error('Order failed:', err);
        alert('Order failed. Please try again.');
      }
    });
    window.location.reload();
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

  updateQuantity(productId: string, change: number): void {
    const productIndex = this.products.findIndex(p => p.productId === productId);
    if (productIndex === -1) return;

    const product = this.products[productIndex];
    const token = localStorage.getItem('jwt');
    const userId = this.userName;

    if (!token || !userId) {
      alert('User not logged in');
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    if (change === 1) {
      // ➕ Add to cart
      const payload = {
        userId: userId,
        products: [productId]
      };

      this.http.put('http://localhost:8080/kart/v1/addproducts', payload, { headers }).subscribe({
        next: () => {
          product.quantity += 1;
          this.kartData.products[productId] = product.quantity;
        },
        error: err => {
          console.error('❌ Failed to add product:', err);
          alert('Failed to add product');
        }
      });

    } else if (change === -1) {
      // ➖ Remove one from cart
      const url = `http://localhost:8080/kart/v1/delete?userId=${userId}&productId=${productId}`;

      this.http.delete(url, { headers }).subscribe({
        next: () => {
          product.quantity -= 1;

          if (product.quantity === 0) {
            this.products.splice(productIndex, 1);
            delete this.kartData.products[productId];
          } else {
            this.kartData.products[productId] = product.quantity;
          }
        },
        error: err => {
          console.error('❌ Failed to remove product:', err);
          alert('Failed to remove product');
        }
      });
    }
    window.location.reload();
  }

 get totalRewardPoints() {
  return this.selectedProducts.reduce((sum, p) => {
    const qty = this.kartData?.products?.[p.productId] || 1;
    return sum + (p.usageRewardPoints || 0) * qty;
  }, 0);
}


  logout(): void {
    this.router.navigate(['/login']);
  }

goToPayments(): void {
  const role = localStorage.getItem('role');
  localStorage.setItem('role', role);
  const token = localStorage.getItem('token');
  localStorage.setItem('jwt', token ?? '');
  const userName = localStorage.getItem('username');
  localStorage.setItem('userName', userName ?? '');
  this.router.navigate(['/payment']);
}

goToOrders(): void {
  const role = localStorage.getItem('role');
  localStorage.setItem('role', role);
  const token = localStorage.getItem('token');
  localStorage.setItem('jwt', token ?? '');
  const userName = localStorage.getItem('username');
  localStorage.setItem('userName', userName ?? '');
  this.router.navigate(['/order']);
}

goToUserHome() {
  const role = localStorage.getItem('role');
  localStorage.setItem('role', role);
  const token = localStorage.getItem('token');
  localStorage.setItem('jwt', token ?? '');
  const userName = localStorage.getItem('username');
  localStorage.setItem('userName', userName ?? '');
  this.router.navigate(['/products']);
}


  
}
