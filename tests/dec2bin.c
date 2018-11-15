#include "minic-stdlib.h"
void main() {
     int n; int i; int j; int binno; int dn;
     binno = 0;

     print_s((char*)"\n\nConvert Decimal to Binary:\n ");

     print_s((char*)"Enter a number to convert : ");
     n = read_i();

     dn=n;
     i=1;
     j = n;



       while (j > 0) {
         binno = binno + (n%2) * i;
         i = i*10;
         n = n/2;
         j = j/2;
       }

     print_s((char*)"\nThe Binary of ");
     print_i(dn);
     print_s((char*) " is ");
     print_i(binno);
     print_s((char*) ". \n\n");
 }
