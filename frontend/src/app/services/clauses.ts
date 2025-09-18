import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

export interface ClauseOutput {
  uuid: string;
  name: string;
  expression: Expression;
}

// TODO: Skal genereres ud fra yml-schema.
// TODO: Gælder hele klient-delen.
export type Expression = StringCondition | NumberCondition | BinaryExpression;

export interface StringCondition {
  type: 'StringCondition';
  field: string;
  value: string;
}

export interface NumberCondition {
  type: 'NumberCondition';
  field: string;
  operator: '=' | '>=' | '<=' | '>' | '<';
  value: number;
}

export interface BinaryExpression {
  type: 'BinaryExpression';
  left: Expression;
  operator: 'AND' | 'OR';
  right: Expression;
}

@Injectable({
  providedIn: 'root'
})
export class ClausesService {
  private http = inject(HttpClient);
  private base = environment.apiBaseUrl;

  getClauses(opts?: { offset?: number; limit?: number }): Observable<ClauseOutput[]> {
    let params = new HttpParams();
    if (opts?.offset != null) params = params.set('offset', opts.offset);
    if (opts?.limit != null)  params = params.set('limit',  opts.limit);

    return this.http.get<ClauseOutput[]>(`${this.base}/2025/08/01/clauses`, { params });
  }
}