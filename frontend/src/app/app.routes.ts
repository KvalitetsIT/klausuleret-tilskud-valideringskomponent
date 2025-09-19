import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Hello} from './features/hello/hello';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'hello', component: Hello }
];