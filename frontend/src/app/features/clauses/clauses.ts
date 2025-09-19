// src/app/features/clauses/clauses.component.ts
import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ClausesService, ClauseOutput } from '../../services/clauses';
import { DslHighlightPipe } from '../../shared/dsl-highlight-pipe';


// TODO: Der mangler tests.
@Component({
  selector: 'app-clauses',
  standalone: true,
  imports: [DslHighlightPipe],
  template: `
    <div class="content">
      <div>
        <h1>Klausuler</h1>

        @if (clauses()?.length === 0) {
          <p>Henter…</p>
        } @else {
          <table>
            <thead>
              <tr><th>Klausul</th><th>Regel</th></tr>
            </thead>
            <tbody>
              @for (c of clauses(); track c.uuid) {
                <tr>
                  <td>{{ c.name }}</td>
                  <td>
                    <code class="dsl" [innerHTML]="(summarize(c.expression) | dslHighlight)"></code>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="3">Ingen klausuler.</td></tr>
              }
            </tbody>
          </table>
        }
      </div>
    </div>
  `,
  styles: [`
    table { width:100%; border-collapse: collapse; }
    th, td { padding:.5rem .75rem; border-bottom:1px solid #e5e7eb; text-align:left; }
    th { font-weight:600; }
  `]
})
export class Clauses{
  private svc = inject(ClausesService);

  // Signals i stedet for async pipe
  clauses = toSignal<ClauseOutput[]>(
    this.svc.getClauses()
  );

  summarize(expr: any): string {
    if (!expr) return '';
    if (expr.type === 'StringCondition') return `${expr.field} = "${expr.value}"`;
    if (expr.type === 'NumberCondition') return `${expr.field} ${expr.operator} ${expr.value}`;
    if (expr.type === 'BinaryExpression') return `(${this.summarize(expr.left)}) ${expr.operator} (${this.summarize(expr.right)})`;
    return '';
  }
}
