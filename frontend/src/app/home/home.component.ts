import { Component, OnInit, ViewChild } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  users: any[] = [];

  displayedColumns: string[] = ['fullName', 'userName', 'emailId', 'mobileNum', 'city', 'role', 'actions'];
  dataSource: MatTableDataSource<any> = new MatTableDataSource<any>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;


  constructor(private http: HttpClient, private router: Router, private authService: AuthService) { }


  ngOnInit(): void {
    this.http.get<any>('http://localhost:8080/user/v1/all').subscribe({
      next: (response) => {
        const users = response.data[0];
        this.dataSource.data = users;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (err) => {
        console.error('Error fetching users', err);
      }
    });
  }


  onEdit(user: any) {
    this.router.navigate(['/updateuser'], {
      state: {
        user: user,
        isFromAdmin: true   // or use returnTo: 'admin'
      }
    });
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
     const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
    console.log("In Home Component :" + userName + " " + token);
    this.router.navigate(['/products']); // passing username in route
  }

  addNewProduct(): void {
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
    console.log("In Home Component :" + userName + " " + token);
    this.router.navigate(['/createproduct']);
  }

  userAvatarFallback(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/profilepics/default.jpeg';
  }

  viewOrderHistory(){
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
    console.log("In Home Component :" + userName + " " + token);
    this.router.navigate(['/order']);

  }


  goToWallet() {
     const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
    console.log("In Home Component :" + userName + " " + token);
    this.router.navigate(['/wallet']);
  }

  goToPayment() {
     const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
    console.log("In Home Component :" + userName + " " + token);
    this.router.navigate(['/payment']);
  }

  gotoKart() {
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('username');
    localStorage.setItem('userName', userName ?? '');
    localStorage.setItem('jwt', token ?? '');
    console.log("In Home Component :" + userName + " " + token);
    this.router.navigate(['/kart']);
  }
searchText = '';

applyFilter(event: Event) {
  const value = (event.target as HTMLInputElement).value.trim().toLowerCase();
  this.searchText = value;
  this.dataSource.filter = value;
}

clearSearch() {
  this.searchText = '';
  this.dataSource.filter = '';

}


}

