import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-update-user',
  templateUrl: './updateuser.component.html',
  styleUrls: ['./updateuser.component.css']
})
export class UpdateUserComponent implements OnInit {
  user: any = {};
  signupError: string = '';
  isFromAdmin: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const state = history.state;

    if (state && state.user) {
      this.user = state.user;
      this.isFromAdmin = state.isFromAdmin ?? false;
      console.log('✅ Loaded user:', this.user);
    } else {
      console.error('❌ No user in navigation state');
    }
  }

  update(): void {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    console.log(this.user);

    this.http.put(`http://localhost:8080/user/v1`, this.user, { headers }).subscribe({
      next: () => {
        alert('User updated successfully!');
        if (this.isFromAdmin) {
          this.router.navigate(['/home']);
        } else {
          this.router.navigate(['/userhome']);
        }
      },
      error: (err) => {
        console.error(err);
        this.signupError = 'Failed to update user';
      }
    });
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  profileFallback(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/profilepics/default.jpeg';
  }

  onProfilePicChange(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const reader = new FileReader();

      reader.onload = (e: any) => {
        const imgEl = document.querySelector('.profile-picture') as HTMLImageElement;
        if (imgEl) {
          imgEl.src = e.target.result; // Preview image
        }
      };

      reader.readAsDataURL(file);
    }
  }
}
