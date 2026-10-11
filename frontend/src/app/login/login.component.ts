import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  login() {
  this.auth.login(this.username, this.password).subscribe({
    next: () => {
      localStorage.setItem('username', this.username);
      const username = localStorage.getItem('username');
      console.log("UserName stored in Login Page : "+username);
      this.auth.getUserRoleFromAPI(username).subscribe({
        next: (role) => {
          localStorage.setItem('role', role)
          if (role) {
            this.router.navigate(['/products']);
          }
          else {
            this.error = 'Unknown role';
          }
        },
        error: (err) => {
          this.error = 'Failed to fetch role';
        }
      });
    },
    error: () => {
      this.error = 'Login failed';
    }
  });
}
goToSignup(): void {
  this.router.navigate(['/signup']);
}

}
