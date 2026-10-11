import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  error = '';
  signupError = '';

  user = {
    firstName: '',
    lastName: '',
    userName: '',
    password: '',
    emailId: '',
    mobileNum: '',
    houseNo: '',
    landmark: '',
    city: '',
    pincode: '',
    role: ''
  };

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    this.http.post('http://localhost:8080/user/v1/post', this.user).subscribe({
      next: () => {
        alert('Signup successful!');
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Signup failed:', error);
        this.error = 'Signup failed. Please check your input or try again.';
      }
    });
  }

  // ✅ Fallback if profile picture doesn't exist
  profileFallback(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = 'assets/profilepics/default.jpg';
  }

  previewUrl: string | ArrayBuffer | null = null;

onFileSelected(event: Event): void {
  const fileInput = event.target as HTMLInputElement;
  const file = fileInput.files?.[0];

  if (file) {
    const reader = new FileReader();
    reader.onload = () => {
      this.previewUrl = reader.result;
    };
    reader.readAsDataURL(file);
  }
}

}
