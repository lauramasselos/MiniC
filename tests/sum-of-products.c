#include "minic-stdlib.h"

int mul(int a, int b, int c) {
  return a * b * c;
}

int sumOfProducts(int a, int b, int c, int d, int e, int f) {
  return mul(a, b, c) + mul(d, e, f);
}


void main() {
  int m; int n; int o;
  int p; int q; int r;
  int resSumOfProducts; //int resMul;
  m = read_i(); n = read_i(); o = read_i();
  p = read_i(); q = read_i(); r = read_i();
  resSumOfProducts = sumOfProducts(m, n, o, p, q, r);

  print_i(resSumOfProducts);
}
