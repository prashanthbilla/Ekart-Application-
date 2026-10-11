import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'order',
  templateUrl: './order.component.html',
  styleUrl: './order.component.css',
})
export class OrderComponent implements OnInit {
  orders: any[] = [];

  constructor(private http: HttpClient, private router: Router) { }

  ngOnInit(): void {
    const userId = localStorage.getItem('userName') || '';
    const token = localStorage.getItem('jwt') || '';

    if (!userId || !token) {
      alert("Not logged in");
      this.router.navigate(['/login']);
      return;
    }

    const role = localStorage.getItem('role');

    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    if (role == "ROLE_USER") {

      this.http.get<any>(`http://localhost:8080/order/v1/getAll?mode=USER_ID&userId=`+userId, { headers }).subscribe({
        next: (res) => {
          this.orders = res.data;
          console.log('✅ Orders loaded: with NONE & USERID', this.orders);
        },
        error: (err) => {
          console.error('❌ Failed to load orders:', err);
          alert('Could not load order history.');
        }
      });

    } else {

        this.http.get<any>(`http://localhost:8080/order/v1/getAll?mode=NONE`, { headers }).subscribe({
        next: (res) => {
          this.orders = res.data;
          console.log('✅ Orders loaded: with NONE', this.orders);
        },
        error: (err) => {
          console.error('❌ Failed to load orders:', err);
          alert('Could not load order history.');
        }
      });
      

    }
  }

  searchTerm: string = '';
  currentPage: number = 1;
  pageSize: number = 5;

  filteredOrders() {
    if (!this.orders) return [];
    return this.orders.filter(order =>
      order.orderId?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      order.userFullName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      order.paymentStatus?.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  totalPages(): number {
    return Math.ceil(this.filteredOrders().length / this.pageSize);
  }

  prevPage() {
    if (this.currentPage > 1) this.currentPage--;
  }

  nextPage() {
    if (this.currentPage < this.totalPages()) this.currentPage++;
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}

