1. In order to run the application, user is needed to unzip  the file
1. User is needed to give (at least) one input - the K value from command line and then just run the program. The related output and
	applications, results will be displayed on console
2. In order to make comfortable I made the application full generic, only depending on dataset and its parsing issues
3. Another input values are given by default, but can be changed from com.end507.project1.shared.ShareClass's static variables
  3.1. Max number of iterations until convergence
  3.2 Max number of swapping iterations after convergence - how many times to do swapping
  3.3 Is user wants to output results to file or console( console is default )
  3.4 User can change the ratio of points to be swapped(1/2 , 1/14, etc)
4. By default the input location is, location_of_app/data/input/household_power_consumption.txt
5. By default the output location is(if user want to output to file), location_of_app/data/output/output.txt
6. To ensure that Java heap space will not crush,increase Java Heap Space by, (by default it do not crashes)
	6.1 If user runs from eclipse:From run->configurations,add -Xms1024M -Xmx1524M to VM arguments
	6.2 If user runs program from commandline, run as java -Xms1024M -Xmx1524M HSMProject1
7. For low K numbers, StandardKMeans acts better, but as K gets bigger, Heuristic Version of KMeans acts better.
	
	