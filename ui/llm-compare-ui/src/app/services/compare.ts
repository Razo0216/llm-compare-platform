import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProviderResult {
  provider: string;
  status: string;
  response: string;
  latencyMs: number;
}

export interface CompareResponse {
  requestId: string;
  results: ProviderResult[];
}

@Injectable({
  providedIn: 'root'
})
export class CompareService {

  private readonly API_URL = 'http://localhost:8082/v1/compare';

  constructor(private http: HttpClient) {}

  compare(prompt: string): Observable<CompareResponse> {
    return this.http.post<CompareResponse>(this.API_URL, { prompt });
  }
}
