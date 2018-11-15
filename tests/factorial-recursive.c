#include "minic-stdlib.h"

// factorial(int);

int factorial(int n) {
  if (n == 0)
    return 1;
  else
    return(n * factorial(n-1));
}

int main() {
  int n;
  int f;

  print_s((char*)"Enter an integer to find its factorial\n");
  n = read_i();

  if (n < 0)
    print_s((char*)"Factorial of negative integers isn't defined.\n");
  else {
    f = factorial(n);
    print_i(n);
    print_s((char*) "! = ");
    print_i(f);
    print_c('\n');

  }

  return 0;
}
