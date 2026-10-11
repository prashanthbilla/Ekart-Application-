import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { HomeComponent } from './home/home.component';
import { UserhomeComponent } from './userhome/userhome.component';
import { UpdateUserComponent } from './updateuser/updateuser.component';
import { KartComponent } from './kart/kart.component';
import { CreateproductComponent } from './createproduct/createproduct.component';
import { ProductComponent } from './product/product.component';
import { UpdateproductComponent } from './updateproduct/updateproduct.component';
import { OrderComponent } from './order/order.component';
import { PaymentComponen } from './payment/payment.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'home', component: HomeComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'userhome', component: UserhomeComponent },
  { path: 'updateuser', component: UpdateUserComponent },
  {  path: 'kart', component: KartComponent },
   {  path: 'products', component: ProductComponent },
  {  path: 'createproduct', component: CreateproductComponent },
  {  path: 'updateproduct', component: UpdateproductComponent },
  {  path: 'order', component: OrderComponent },
  {  path: 'payment', component: PaymentComponen }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
