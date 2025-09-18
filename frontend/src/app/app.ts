import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import ClausesComponent from './features/clauses/clauses.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ClausesComponent],
  templateUrl: './app.html'
})
export class App {
  protected readonly title = signal('itukt-gui');
}
