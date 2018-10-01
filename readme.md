[![Maven Central](https://maven-badges.herokuapp.com/maven-central/fr.lirmm.graphik/graal-elder/badge.svg)](https://maven-badges.herokuapp.com/maven-central/fr.lirmm.graphik/graal-elder)

# ELDR (Existential Logic for Defeasible Reasoning)
This a Defeasible Logic reasonner for Existential rules, it allows for Ambiguity Blocking or Propagating, with or without Team Defeat.

## Getting Started
You can either import this module using Maven Central (recommended). Add the following to your pom.xml:
```
<dependency>
      <groupId>fr.lirmm.graphik</groupId>
      <artifactId>graal-elder</artifactId>
      <version>1.0.0</version>
</dependency>
```

Or using Github then adding it to your project:
```
> git clone https://github.com/hamhec/graal-elder.git
```
### Example

See [Graal-Defeasible-Core](https://github.com/hamhec/graal-defeasible-core) for more information on the format.

```
kb.add("penguin(kowalski), bird(tweety), brokenWings(tweety).");
kb.add("beautiful(X) <- penguin(X).");
kb.add("sad(X) <- brokenWings(X).");

kb.add("notFly(X), bird(X) <- penguin(X).");
kb.add("[rfly] fly(X) <= bird(X).");
kb.add("[rbroken] notFly(X) <~ brokenWings(X) .");

kb.add("! :- fly(X), notFly(X).");

kb.add("rbroken >> rfly .");

// Statement Graph takes a knowledge base and a labeling function, BDLwithoutTD means Blocking with no Team Defeat
StatementGraph sg = new StatementGraph(kb, new BDLwithoutTD(kb));

sg.build();

String answer = sg.groundQuery("fly(kowalski)"); // does kowalski fly?
System.out.println(answer); // OUT

answer = sg.groundQuery("notFly(kowalski)"); // does kowalski not fly?
System.out.println(answer); // DEFEASIBLE_IN

answer = sg.groundQuery("fly(tweety)"); // does kowalski fly?
System.out.println(answer); // OUT

answer = sg.groundQuery("notFly(tweety)"); // does kowalski not fly?
System.out.println(answer); // UNSUPPORTED

answer = sg.groundQuery("happy(kowalski)"); // does kowalski fly?
System.out.println(answer); // UNSUPPORTED
```

### Publications and Theory
Defeasible Reasoning intuitions, semantics and Proofs for equivalence between SG labelings and Defeasible Logics can be found in [THESIS](theory.pdf) (Chapter 2, 4, and 7).

### Known Issues

ELDR does not currently support cycles (positive and negative loops). We are currently working on it.

## Authors

* **Abdelraouf Hecham** - *Developement and Maintenance* - [Hamhec](https://github.com/hamhec)

## License

This project is licensed under CeCILL 2.1 - see the [LICENSE](LICENSE) file for details
