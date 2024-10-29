import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        const specificApiUrl = '/api/specific-endpoint'; // Replace with your specific API endpoint
        
        // Check if the request URL contains the specific API endpoint
        if (req.url.includes(specificApiUrl) && error.status === 404) {
          // Suppress or handle 404 error specifically for the matched API
          console.warn(`404 error suppressed for ${specificApiUrl}`);
          // Optionally, handle the error (e.g., send a user-friendly message, redirect, etc.)
          return throwError(() => new Error('Specific API not found; please try again later.'));
        } 
        
        // Handle other errors or URLs differently if needed
        console.error('An error occurred:', error);
        return throwError(() => new Error('Something went wrong; please try again later.'));
      })
    );
  }
}