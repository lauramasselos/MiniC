#include "minic-stdlib.h"

int n;
void main() {
  n = 5;
  while (n > 0) {
      print_s((char*) "I'm stuck here!");
      while (n > 4) {
        print_s((char*) "I'm nested");
        while (n > 3) {
          print_s((char*) "I'm also stuck.");
        }
      }
  }

}
