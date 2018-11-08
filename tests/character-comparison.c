#include "minic-stdlib.h"

char a;
char b;

void main() {
  a = 'a';
  b = 'b';
  while (a == 'a') {
    print_c(a);
    a = 'b';
  }
  while (a == b) {
    print_c(b);
    a = 'c';
  }
}
