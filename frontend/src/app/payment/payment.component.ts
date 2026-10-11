import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'payment',

  templateUrl: './payment.component.html',
  styleUrl: './payment.component.css'
})
export class PaymentComponen implements OnInit {
  paymentData: any[] = [];
  searchTerm: string = '';
  currentPage: number = 1;
  pageSize: number = 5;
  userId: string = '';

  constructor(private http: HttpClient, private router: Router) { }

  ngOnInit(): void {
    const token = localStorage.getItem('jwt');
    const storedUser = localStorage.getItem('userName');

    if (!token || !storedUser) {
      alert('User not logged in. Redirecting to login...');
      this.router.navigate(['/login']);
      return;
    }

    this.userId = storedUser;
      const role = localStorage.getItem('role');

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    if (role == "ROLE_USER") {

      this.http.get<any>(`http://localhost:8080/payment/v1/getAll?mode=USER_ID&userId=`+this.userId, { headers }).subscribe({
        next: (res) => {
          console.log('✅ Payment data:', res);
          if (Array.isArray(res.data[0])) {
            this.paymentData = res.data[0]; // Flattened
          } else {
            this.paymentData = res.data || [];
          }
        },
        error: (err) => {
          console.error('❌ Error fetching payments:', err);
          alert('Failed to fetch payment history. Please login again.');
          this.router.navigate(['/login']);
        }
      });


    } else {

      // Fetch payment history for this user
      this.http.get<any>(`http://localhost:8080/payment/v1/getAll?mode=NONE`, { headers }).subscribe({
        next: (res) => {
          console.log('✅ Payment data:', res);
          if (Array.isArray(res.data[0])) {
            this.paymentData = res.data[0]; // Flattened
          } else {
            this.paymentData = res.data || [];
          }
        },
        error: (err) => {
          console.error('❌ Error fetching payments:', err);
          alert('Failed to fetch payment history. Please login again.');
          this.router.navigate(['/login']);
        }
      });
    }
  }

  // Filter logic
  filteredPayments() {
    const term = this.searchTerm.toLowerCase();
    return this.paymentData.filter(p =>
      (p.paymentId || '').toLowerCase().includes(term) ||
      (p.userFullName || '').toLowerCase().includes(term) ||
      (p.paymentStatus || '').toLowerCase().includes(term) ||
      (p.paymentMode || '').toLowerCase().includes(term)
    );
  }

  totalPages(): number {
    return Math.ceil(this.filteredPayments().length / this.pageSize);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages()) this.currentPage++;
  }

  prevPage(): void {
    if (this.currentPage > 1) this.currentPage--;
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}