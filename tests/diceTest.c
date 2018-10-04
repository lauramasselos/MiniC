// rolls two dice; player wins if 10 or higher rolled

#include "minic-stdlib.h"

int roll(void) {
  int r = rand();
  int roll = (r % 6) + 1;
  return roll;
}


int main() {
  printf("Roll dice? [Yes = 1/No = 0]\n");
  int n = read_i();
  if (n == 1) {
    int d1 = roll();
    int d2 = roll();
    int sum = d1 + d2;
    printf("You rolled %2d + %2d = %2d.\n", d1, d2, sum);
    if (sum >= 10) printf("Congratulations, you won!\n");
    else printf("Try again! :(\n");
  }
  else {
        printf("Game ended.\n");
  }

return 0;
}
