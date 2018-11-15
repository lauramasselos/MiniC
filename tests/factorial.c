#include "minic-stdlib.h"

int main() {
  int c; int n; int fact;
  c = 1;
  n = 1;
  fact = 1;
  print_s((char*)"Enter a number to calculate its factorial\n");
  n = read_i();


  while (c <= n) {
    fact = fact * c;
    c = c + 1;
  }

  print_s((char*)"Factorial of ");
  print_i(n);
  print_c('=');
  print_i(fact);
  print_c('\n');
  return 0;
}
