import { Component, inject } from '@angular/core';
import { Clauses } from '../../features/clauses/clauses';
import { App } from '../../app';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [Clauses],
  templateUrl: './home.html'
})
export class Home {
  app = inject(App);
}
