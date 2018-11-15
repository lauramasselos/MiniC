#include "minic-stdlib.h"


int add(int a, int b, int c) {
  return a + b + c;
}

void main() {
  int m; int n; int o; int res;
  m = read_i(); n = read_i(); o = read_i();
  res = add(m, n, o);
  print_i(res);
}
