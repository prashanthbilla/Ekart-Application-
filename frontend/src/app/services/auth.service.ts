import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { tap, map, catchError } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode';

interface JwtPayload {
  sub: string;
  role: string | string[];
  exp: number;
  iat: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private selectedUser: any;

  setSelectedUser(user: any): void {
    this.selectedUser = user;
  }

  getSelectedUser(): any {
    return this.selectedUser;
  }


  userName = '';
  private authUrl = 'http://localhost:8080/auth/v1/authenticate';

  constructor(private http: HttpClient, private router: Router) { }

  private selectedUsername: string = '';

  getUserName(): string {
    return this.selectedUsername || localStorage.getItem('username') || '';
  }

  // ✅ LOGIN
  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(this.authUrl, { username, password }).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
       // 👈 store username
      })
    );
  }


  getUserRoleFromAPI(username: string): Observable<string> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<any>(`http://localhost:8080/user/v1/getRole?userName=${username}`, { headers })
      .pipe(
        map(response => response.data) // data contains role
      );
  }


  // ✅ LOGOUT
  logout(): void {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  // ✅ GET USERNAME FROM TOKEN
  getUsername(): string {
    const token = localStorage.getItem('token');
    if (!token) return '';
    try {
      const decoded = jwtDecode<JwtPayload>(token);
      return decoded.sub || '';
    } catch {
      return '';
    }
  }
}
