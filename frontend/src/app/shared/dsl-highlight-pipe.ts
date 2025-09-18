import { Pipe, PipeTransform, inject } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';


// TODO: Opret tests
@Pipe({
  name: 'dslHighlight',
  standalone: true
})
export class DslHighlightPipe implements PipeTransform {
  private sanitizer = inject(DomSanitizer);

  transform(input: string | null | undefined): SafeHtml {
    const src = (input ?? '').toString();

    // Vi escaper HTML'en
    const esc = src.replace(/&/g, '&amp;')
                   .replace(/</g, '&lt;')
                   .replace(/>/g, '&gt;');

    let out = esc;


    // operators
    out = out.replace(/(>=|<=|=|>|<)/gi, '<span class="op">$1</span>');

    // keywords
    out = out.replace(/\b(AND|OR)\b/g, '<span class="k">$1</span>');

    // numbers
    out = out.replace(/\b\d+(?:\.\d+)?\b/g, '<span class="num">$&</span>');

    // identifiers
    out = out.replace(/\b[A-Z][A-Z0-9_]*\b/g, '<span class="id">$&</span>');

    // punctuation
    out = out.replace(/[:(),]/g, '<span class="p">$&</span>');

    return this.sanitizer.bypassSecurityTrustHtml(out);
  }
}