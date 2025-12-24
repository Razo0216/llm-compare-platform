import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CompareService, ProviderResult, CompareResponse } from '../../services/compare';



@Component({
  selector: 'app-compare',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './compare.html',
  styleUrl: './compare.css'
})

export class Compare {

  prompt = '';
  loading = false;
  results: ProviderResult[] = [];

  constructor(private compareService: CompareService) {}

  compare() {
    this.loading = true;
    this.results = [];

    this.compareService.compare(this.prompt).subscribe({
      next: (res: CompareResponse) => {
        this.results = res.results;
        this.loading = false;
      },
      error: (err: unknown) => {
        console.error(err);
        this.loading = false;
        alert('Request failed');
      }
    });
  }
}
