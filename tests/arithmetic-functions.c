#include "minic-stdlib.h"


int add(int a, int b, int c) {
  return a + b + c;
}

int mul(int a, int b, int c) {
  return a * b * c;
}

void main() {
  int m; int n; int o; int resAdd; int resMul;
  m = read_i(); n = read_i(); o = read_i();
  resAdd = add(m, n, o);
  resMul = mul(m, n, o);
  print_i(resAdd); print_c('\n'); print_i(resMul);
}
