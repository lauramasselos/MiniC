#include "minic-stdlib.h"

struct s1 {
  int i;
  int j;
  int k;
  // int l;
  // int m;
  // int n;
  // int o[17]; // 17*4 = 68
};



// struct s2 {
//   char a;
//   char b;
//   char c;
// };
struct s1 n;

// struct s2 n2;

// struct s1 foo1() {
//   n.i = 5;
//   n.j = 6;
//   n.k = 7;
//   return n;
// }
//
void main() {
  n.i = 1;
  n.j = 2;
  n.k = 3;

  print_i(n.i); print_c('\n');
  print_i(n.j); print_c('\n');
  print_i(n.k); print_c('\n');

}
