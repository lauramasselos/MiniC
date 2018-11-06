#include "minic-stdlib.h"

int n;
void main() {
  n = 5;
  while (n > 0) {
      print_s((char*) "I'm stuck here!");
  }

}
