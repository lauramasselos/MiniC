#include "minic-stdlib.h"

int a;
int b;
int c;

int main() {


a = 15 * (4 - 2) * 3; // 90
b = a + 4; // 94
c = (74*b) / (5*a) % b; // 94 / 90 % 90

print_i(a); print_c('\n');
print_i(b); print_c('\n');
print_i(c); print_c('\n');

}
