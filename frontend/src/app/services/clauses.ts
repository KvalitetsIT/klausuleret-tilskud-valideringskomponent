import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ManagementService } from '@api/api/management.service';
import { ClauseOutput } from '@api/model/clauseOutput'; 

@Injectable({ providedIn: 'root' })
export class ClausesService {
  private api = inject(ManagementService);

  // Hent alle clauses
  getClauses(): Observable<ClauseOutput[]> {
    // Her anvendes default-udgave der blot returnerer 'body'
    return this.api._20250801clausesGet()
  }
}
