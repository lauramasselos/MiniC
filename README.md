# Deadlines #

1. [Part 1 (parser)](desc/part1/), Thursday 12 October 2017  at 4pm, weight = 20%
2. Part 2 (ast builder + semantic analyser),  Thursday 26 October 2017  at 4pm, weight = 20%
3. Part 3 (code generator), Thursday 16 November 2017 at 4pm, weight = 30%
4. Part 4 (LLVM-based compiler pass), Monday 15 January 2018, 10am, weight = 30%

Note that specific instructions for each part can be found above by clicking on the part name.

# Scoreboard #

We automatically run a series of hidden test programs using your compiler about once a day.
You can keep track of your progress and see how many tests pass/fail using the scoreboard by following this link: TBC

# Marking #

The marking will be done using an automated test suite on a dice machine using Java 8 (1.8 runtime).
Please note that you are not allowed to modify the `Main.java` file which is the main entry point to the compiler.
A checksum on the file will be performed to ensure the file has not be tempered with.
Also make sure that the build script provided remains unchanged so that your project can be built on dice.
Furthermore, you may not use any external libraries.

For parts 1-3 of the coursework, the marking will be a function of the number of successful tests as shown in the scoreboard and a series of hidden tests.

## Part 1-2
66% of the mark will be determined by the scoreboard tests and 33% will be determined by the hidden tests.
You will get one point for each passing test and -1 for each failing test.
Then the mark is calculated by dividing the number of points achieved by the number of tests.
To be more precise, here is the formula used for marking: 
2/3\*max(0,(#passed_v_tests-#failed_v_tests)/(#passed_v_tests+#failed_v_tests)) + 1/3\*max(0,(#passed_h_tests-#failed_h_tests)/(#passed_h_tests+#failed_h_tests))
where _v_ represents the visible tests and _h_ the hidden ones.

## Part 3
66% of the mark will be determined by the visible scoreboard tests and 33% will be determined by the hidden tests.
The mark will directly be proportial to the number of passed tests (no negative point).
So for instance, passing 7 out of 50 tests would result in a mark of 14/100.


# Setup #

## GitLab ##
We will rely on gitlab and it is mandatory to use it for this coursework.
GitLab is an online repository that can be used with the git control revision system.  The university runs a GitLab hosting service, and all students are provided with an account. The username is your univeristy id number (sXXXXXXX) and your password is the EASE.

Important: do not share your code and repository with anyone and keep your source code secret.
If we identify that two students have identical portion of code, both will be considered to have cheated.

## Development environment (editor)
You can choose to use a development environment for your project. DICE machines have the following integrated development environments (IDE) for Java installed:

* Community edition of [IntelliJ](https://www.jetbrains.com/idea/).
* [Eclipse](https://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/marsr) for Java.

Alternatively, you can use Emacs, vim, or your favourite text editor. Choose whichever you are confident with.

## Obtaining your own copy of the ct-18-19 repository
We are going to be using the Git revision control system during the course. Git is installed on DICE machines. If you use your own machine then make sure to install Git.

You can find the base CT-18-19 repository [here](https://git.ecdf.ed.ac.uk/cdubach/ct-18-19)

You will need to have your own copy of the ct-18-19 repository. In order to fork this repository, click the fork button:

![Forking the CT-18-19 repository](/figures/gl_fork1.png "Forking this repository.")

![Forking the CT-18-19 repository](/figures/gl_fork2.png "Forking this repository.")

Then, make the repository private

![Making repository private](/figures/gl_private1.png "Making repository private.")

![Making repository private](/figures/gl_private2.png "Making repository private.")

![Making repository private](/figures/gl_private3.png "Making repository private.")

Now, grant access to the teaching staff

![Granting the teaching staff read access](/figures/gl_permissions1.png "Granting the teaching staff read access.")

![Granting the teaching staff read access](/figures/gl_permissions2.png "Granting the teaching staff read access.")

You should grant the following users *write* access:
  * Christophe Dubach (username: cdubach)
  * Federico Pizzuti (username: s1580329)

Next, you will have to clone the forked repository to your local machine. In order to clone the repository you should launch a terminal and type:

```
$ git clone https://sXXXXXXX@git.ecdf.ed.ac.uk/sXXXXXXX/ct-18-19.git
```

where sXXXXXXX is your student id

## Building the ct-18-19 project
In order to build the project you must have Ant installed. On DICE machines Ant is already installed.
Your local copy of the ct-18-19 repository contains an Ant build file (`build.xml`).
If you are using an IDE, then you can import the build file.
Otherwise, you can build the project from the commandline by typing:
```
$ ant build
```
This command outputs your compiler in a directory called `bin` within the project structure. Thereafter, you can run your compiler from the commandline by typing:
```
$ java -cp bin Main
```
The parameter `cp` instructs the Java Runtime to include the local directory `bin` when it looks for class files.

You can find a series of tests in the `tests` folder. To run the lexer on one of them, you can type:

```
$ java -cp bin Main -lexer tests/fibonacci.c
```


You can clean the `bin` directory by typing:
```
$ ant clean
```
This command effectively deletes the `bin` directory.

## Working with git and pushing your changes

Since we are using an automated marking mechnism (based on how many progams can run successfully through your compiler), it is important to understand how git works. If you want to benefit from the nightly automatic marking feedback, please ensure that you push all your changes daily onto your bitbucket centralised repository.

We suggest you follow the excelent [tutorial](https://www.atlassian.com/git/tutorials/what-is-version-control) from atlassian on how to use git. In particular you will need to understand the following basic meachnisms:

* [add and commit](https://www.atlassian.com/git/tutorials/saving-changes)
* [push](https://www.atlassian.com/git/tutorials/syncing/git-push)
