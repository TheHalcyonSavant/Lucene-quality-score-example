##Lucene quality score example
Get the best matched string of normalised set of strings by comparing their quality scores.

###Task
Provided with a list of target job titles (the normalised set), implement a method which calculates the best match when provided with an un-normalised input string. Concretely, given a normalised list `N {"Architect", "Software engineer", "Quantity surveyor", "Accountant"}`, write a method which returns the following matches (hint: internally in the method, consider a _quality score q_, where 0.0 <= q <= 1.0):
```
un-normalised -> normalised

"Java engineer" -> "Software engineer"
"C# engineer" -> "Software engineer"
"Accountant" -> "Accountant"
"Chief Accountant" -> "Accountant"
```

####Usage and results
```java
String jt = "Java engineer";

Normaliser n = new Normaliser();
String normalisedTitle = n.normalise(jt);
//output normalisedTitle

jt = "C# engineer";
normalisedTitle = n.normalise(jt);
//output normalisedTitle

jt = "Chief Accountant";
normalisedTitle = n.normalise(jt);
//output normalisedTitle
```
