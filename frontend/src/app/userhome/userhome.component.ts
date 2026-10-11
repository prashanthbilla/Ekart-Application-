
import { Component, OnInit, ViewChild } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-userhome',
  templateUrl: './userhome.component.html',
  styleUrls: ['./userhome.component.css']
})
export class UserhomeComponent implements OnInit {

  users: any[] = [];
  isAdmin: boolean = false;

  displayedColumns: string[] = ['fullName', 'userName', 'emailId', 'mobileNum', 'city', 'role', 'actions'];
  dataSource: MatTableDataSource<any> = new MatTableDataSource<any>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;


  constructor(private http: HttpClient, private router: Router, private authService: AuthService) { }
  user: any;


  ngOnInit(): void {
    const role = localStorage.getItem('role');
    this.isAdmin = role === 'ROLE_ADMIN';
    const userName = this.authService.getUsername();
    const userPage = 2;
    console.log(userName);
    this.http.get<any>('http://localhost:8080/user/v1?userName=' + userName).subscribe({

      next: (response) => {
        this.user = response.data[0];    // ✅ use array directly
      },
      error: (err) => {
        console.error('Error fetching user', err);
      }
    });
  }


  applyFilter(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.dataSource.filter = value.trim().toLowerCase();
  }

  onEdit(user: any) {
    console.log(user);
    this.router.navigate(['/updateuser'], {
      state: {
        user: user,
        isFromAdmin: false   // or use returnTo: 'user'
      }
    });
    // You can route to edit page or open modal
  }

  onDelete(user: any) {
      console.log(user)
      const confirmDelete = confirm(`Are you sure you want to delete user: ${user.userName}?`);
      if (!confirmDelete) return;
  
      console.log("user.."+user);
      const token = localStorage.getItem('jwt') || '';
      console.log(token)
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  
      // Call DELETE endpoint using userName
      this.http.delete(`http://localhost:8080/user/v1?userId=${user.userName}`, { headers }).subscribe({
        next: () => {
          alert(`User ${user.userName} deleted successfully`);
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error('Delete failed:', err);
          alert('Failed to delete user');
        }
      });
    }
  logout(): void {
    this.router.navigate(['/login']);
  }

   goToProducts() {
    this.router.navigate(['/products']); // passing username in route
  }

 profileFallback(event: Event): void {
  const img = event.target as HTMLImageElement;
  img.src = 'assets/profilepics/default.jpeg';

}

goToKart() {
  const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
  this.router.navigate(['/kart']);
}

goToWallet() {
  const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
  this.router.navigate(['/wallet']);
}

goToPayments() {
  const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
  this.router.navigate(['/payment']);
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

goToOrders(){
  const role = localStorage.getItem('role');
  localStorage.setItem('role', role);
  const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
  this.router.navigate(['/order']);

}


showModal: boolean = false;

showImagePreview() {
  this.showModal = true;
}

closeImagePreview() {
  this.showModal = false;
}



}




