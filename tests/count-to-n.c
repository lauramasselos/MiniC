#include "minic-stdlib.h"

void main() {
  int n;
  int zero;

  // read n from the standard input
  n = read_i();
  zero = 0;


  print_s((char*)"All the numbers up to ");
  print_i(n); print_c(':'); print_c('\n');

  while (zero < n) {
    print_i(zero); zero = zero + 1; print_c(' ');
  }
  print_i(n);
}
