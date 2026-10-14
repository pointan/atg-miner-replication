package at.fhooe.se.atgminer.semantic.v2;

import at.fhooe.se.atgminer.atg.*;
import at.fhooe.se.atgminer.parsetree.ParseTree;
import at.fhooe.se.atgminer.util.ListUtil;
import at.fhooe.se.atgminer.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Mapping between ParseTree and Syntax Tree</p>
 */
public class PTSTMapping {
    private final List<Pair<ParseTree, ATSyntaxTree>> mappings;
    private ATSyntaxTree stRoot;
    private ParseTree ptRoot;

    public PTSTMapping() {
        this.mappings = new ArrayList<>();
    }

    public void findMappings(ATSyntaxTree syntaxTree, ParseTree parseTree) {
        stRoot = syntaxTree;
        ptRoot = parseTree;
        List<ATSyntaxTree> atSyntaxTreesTerminals = filterTerminals(syntaxTree);
        List<ParseTree> parseTreesTerminals = filterTerminals(parseTree);
        mapTerminals(parseTreesTerminals, atSyntaxTreesTerminals);
        mapUpwards(syntaxTree, parseTree);
    }

    private List<ATSyntaxTree> filterTerminals(ATSyntaxTree syntaxTree) {
        return syntaxTree.stream().filter(ATSyntaxTree::isTerminal).collect(Collectors.toList());
    }

    private List<ParseTree> filterTerminals(ParseTree parseTree) {
        return parseTree.stream().filter(ParseTree::isTerminalSymbol).collect(Collectors.toList());
    }

    private void mapTerminals(List<ParseTree> ptTerminals, List<ATSyntaxTree> stTerminals) {
        if (ptTerminals.size() != stTerminals.size()) {
            listDiff(ptTerminals, stTerminals);
            throw new IllegalStateException("ParseTree terminals does not equals number of SyntaxTree terminals");
        }
        for (int i = 0; i < ptTerminals.size(); i++) {
            ParseTree parseTree = ptTerminals.get(i);
            ATSyntaxTree atSyntaxTree = stTerminals.get(i);
            if (!parseTree.getLabel().equals(atSyntaxTree.getSymbol().getKey())) {
                throw new IllegalStateException("ParseTree terminal does not match SyntaxTree terminal");
            }
            this.add(parseTree, atSyntaxTree);
        }
    }

    private void listDiff(List<ParseTree> ptTerminals, List<ATSyntaxTree> stTerminals) {
        for (int i = 0; i < Math.max(ptTerminals.size(), stTerminals.size()); i++) {
            Optional<ParseTree> parseTree = ListUtil.tryGetIndex(ptTerminals, i);
            Optional<ATSyntaxTree> syntaxTree = ListUtil.tryGetIndex(stTerminals, i);
            if (parseTree.isPresent() && syntaxTree.isPresent() && !syntaxTree.get().getSymbol().getKey().equals(parseTree.get().getLabel())) {
                System.out.println("ERROR: Different at position " + i + " st: " + syntaxTree.get().getSymbol().getKey() + " vs pt: " + parseTree.get().getLabel());
            }
            if (parseTree.isEmpty() && syntaxTree.isPresent()) {
                System.out.println("ERROR: No ParseTree element at pos " + i + " but st: " + syntaxTree.get().getSymbol().getKey());
            }
            if (syntaxTree.isEmpty() && parseTree.isPresent()) {
                System.out.println("ERROR: No SyntaxTree element at pos " + i + " but pt: " + parseTree.get().getLabel());
            }
        }
    }

    private void mapUpwards(ATSyntaxTree syntaxTreeRoot, ParseTree parseTreeRoot) {
        enrichTerms(syntaxTreeRoot);
        enrichTerms(parseTreeRoot);
        directMapping();
        indirectMapping();
        mapUnmapped(parseTreeRoot);
    }

    private void enrichTerms(ATSyntaxTree tree) {
        if (tree.isTerminal()) {
            tree.setReachableTerms(List.of((ATTerminal) tree.getSymbol()));
        } else {
            tree.getChildren().forEach(this::enrichTerms);
            List<ATTerminal> termsOfChildren = tree.getChildren().stream()
                    .map(ATSyntaxTree::getReachableTerms)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            tree.setReachableTerms(termsOfChildren);
        }
    }

    private void enrichTerms(ParseTree tree) {
        if (tree.isTerminalSymbol()) {
            tree.setReachableTerms(List.of(new ATTerminal(tree.getLabel())));
        } else {
            tree.getChildren().forEach(this::enrichTerms);
            List<ATTerminal> termsOfChildren = tree.getChildren().stream()
                    .map(ParseTree::getReachableTerms)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            tree.setReachableTerms(termsOfChildren);
        }
    }

    private void directMapping() {
        // starting points are the lists of terminal symbols
        for (Pair<ParseTree, ATSyntaxTree> parseTreeATSyntaxTreePair : this.getIterableMappingsCopy()) {
            var currentPtElem = parseTreeATSyntaxTreePair.getFirst();
            var currentStElem = parseTreeATSyntaxTreePair.getSecond();

            while (currentStElem != null && currentPtElem != null) {
                // skip pseudo nodes (we will map them later):
                if (currentStElem.isPseudoNode()) {
                    currentStElem = currentStElem.getParent();
                    continue;
                }
                if (currentPtElem.isPseudoNode()) {
                    currentPtElem = currentPtElem.getParent();
                    continue;
                }

                /*
	            Three chases:
	            (1) The reachable terminals match in both paths.
	            (2) The reachable terminals in the parse tree are only a subset of the syntax tree -> go up in parse tree
	            (3) The reachable terminals in the parse tree are a superset of the syntax tree -> go up in syntax tree

	            What is still missing:
	             - Not 100% sure what to do with nodes in the parse tree that are not in the syntax tree (or if only a subset of terminals can be reached) -->
	               Maybe we can use the position of the current terminals, and append it to the closest matching node in the syntax tree
	             - Prefer function over pseudo-function nodes
	             - What if multiple nodes would match, e.g. if for case (1) more paths would be available in the parsetree, we would probably need to peek the next node in both trees, and check if we should go up for both nodes, or if we should stay at one node, and only go up in one of the trees
	            */

                if (currentPtElem.getReachableTerms().equals(currentStElem.getReachableTerms())) {
                    if (!this.containsMapping(currentPtElem, currentStElem)) {
                        this.add(currentPtElem, currentStElem);
                    }
                    currentPtElem = currentPtElem.getParent();
                    currentStElem = currentStElem.getParent();
                } else if (new HashSet<>(currentStElem.getReachableTerms()).containsAll(currentPtElem.getReachableTerms())) {
                    currentPtElem = currentPtElem.getParent();
                } else {
                    currentStElem = currentStElem.getParent();
                }
            }
        }
    }

    private void indirectMapping() {
        for (Pair<ParseTree, ATSyntaxTree> parseTreeATSyntaxTreePair : this.getIterableMappingsCopy()) {
            ParseTree currentPtElem = parseTreeATSyntaxTreePair.getFirst().getParent();
            ParseTree beginPtElem = currentPtElem;
            ATSyntaxTree beginStElem = parseTreeATSyntaxTreePair.getSecond().getParent();

            // find next mapping:
            while(!this.contains(currentPtElem) && currentPtElem != null) {
                currentPtElem = currentPtElem.getParent();
            }
            if (currentPtElem == null) continue;
            ATSyntaxTree nextMapping = this.getMapping(currentPtElem);

            // find paths between:
            List<ParseTree> ptPath = new ArrayList<>();
            List<ATSyntaxTree> stPath = new ArrayList<>();
            while(beginPtElem != currentPtElem) {
                ptPath.add(beginPtElem);
                beginPtElem = beginPtElem.getParent();
            }
            while(beginStElem != nextMapping) {
                stPath.add(beginStElem);
                beginStElem = beginStElem.getParent();
            }

            findBestIndirectMappings(ptPath, stPath);
        }
    }

    private void findBestIndirectMappings(List<ParseTree> ptPath, List<ATSyntaxTree> stPath) {
        // Maybe some heuristics could be applied here, for the moment we just do a simple upwards search:
        List<Pair<ParseTree, ATSyntaxTree>> possibleMappings = new ArrayList<>();
        int stPathIdx = 0;
        for (ParseTree currentPtElem : ptPath) {
            int initialSt = stPathIdx;
            // iterate through the parse tree list and search if a matching syntax tree is available:
            Optional<ATSyntaxTree> currentStElemOpt = ListUtil.tryGetIndex(stPath, stPathIdx);
            while(currentStElemOpt.isPresent() && (!currentPtElem.getReachableTerms().equals(currentStElemOpt.get().getReachableTerms()) || !sameType(currentPtElem, currentStElemOpt.get()))) {
                stPathIdx++;
                currentStElemOpt = ListUtil.tryGetIndex(stPath, stPathIdx);
            }

            // we do not increase stPathIdx here, only if the mapping is applied: (Note: maybe we need another heuristic here to determine if that should be done or not (e.g. if the next level is mappable or similar))
            if (currentStElemOpt.isPresent()) {
                var currentStElem = currentStElemOpt.get();
                possibleMappings.add(new Pair<>(currentPtElem, currentStElem));
                stPathIdx++;
            } else {
                stPathIdx = initialSt;
            }
        }

        // heuristic to check which mapping fits best (simply the one that has a semantic, if multiple do that, no solution yet)
        Map<ATSyntaxTree, List<ParseTree>> collect = possibleMappings.stream().collect(Collectors.toMap(Pair::getSecond, x -> List.of(x.getFirst()), ListUtil::concat));
        collect.forEach((atSyntaxTree, parseTrees) -> {
            if (parseTrees.size() == 1) {
                this.add(parseTrees.get(0), atSyntaxTree);
            } else {
                List<ParseTree> parseTreeWithSems = parseTrees.stream().filter(ParseTree::containsSemantics).collect(Collectors.toList());
                if (parseTreeWithSems.size() > 1) {
                    System.out.println("WARNING: Did not find a unique mapping, as multiple elements fit.");
                } else if(parseTreeWithSems.size() == 1) {
                    this.add(parseTreeWithSems.get(0), atSyntaxTree);
                }
            }
        });
    }

    private boolean sameType(ParseTree ptElem, ATSyntaxTree atSyntaxTree) {
        if (ptElem.isPseudoNode() != atSyntaxTree.isPseudoNode()) return false;
        if (ptElem.getLabel().endsWith("_while")) return atSyntaxTree.getSymbol() instanceof ATRepetition;
        if (ptElem.getLabel().contains("_if_")) return atSyntaxTree.getSymbol() instanceof ATChoice || (atSyntaxTree.getSymbol() instanceof ATOption);
        System.out.println("sameType check default return");
        return false;
    }

    private void mapUnmapped(ParseTree parseTreeRoot) {
        List<ParseTree> unmappedPTElements = findUnmappedPTElements(parseTreeRoot);
        for (ParseTree unmappedPTElement : unmappedPTElements) {
            if (unmappedPTElement.getReachableTerms().isEmpty() && unmappedPTElement.getChildren().isEmpty()) {
                mapEmptyUnreachableTerm(unmappedPTElement);
            } else {
                // for the moment just search downwards, if we do not find anything
                ParseTree searchFirst = unmappedPTElement;
                while (searchFirst != null && !this.contains(searchFirst)) {
                    if (searchFirst.getChildren().isEmpty()) {
                        searchFirst = null;
                    } else {
                        searchFirst = searchFirst.getChildren().get(0);
                    }
                }
                ParseTree searchLast = unmappedPTElement;
                while (searchLast != null && !this.contains(searchLast)) {
                    if (searchLast.getChildren().isEmpty()) {
                        searchLast = null;
                    } else {
                        searchLast = searchLast.getChildren().get(searchLast.getChildren().size() - 1);
                    }
                }
                if (searchFirst != null && searchLast != null) {
//                    System.out.println("INFO: Moved semantics of PT " + unmappedPTElement.getLabel() + " to closest child.");
                    // do not add a mapping here, but really move the semantics accordingly, this should be more accurate
                    ListUtil.moveLists(unmappedPTElement.getPreSemantics(), searchFirst.getPreSemantics(), 0);
                    ListUtil.moveLists(unmappedPTElement.getPostSemantics(), searchLast.getPostSemantics());
                } else {
                    System.out.println("WARNING: Unmapped semantics in PT: " + unmappedPTElement.getLabel());
                }
            }
        }
    }

    private void mapEmptyUnreachableTerm(ParseTree unmappedPTElement) {
        // find term before and after:
        // collect all leaves from parse tree
        List<ParseTree> ptLeaveNodes = ptRoot.stream().filter(pt -> pt.getChildren().isEmpty()).collect(Collectors.toList());
        final int i = ptLeaveNodes.indexOf(unmappedPTElement);
        if (i == -1) {
            throw new IllegalStateException(unmappedPTElement.getKey() + " is not a leaf node. ");
        }

        int prevI = i;
        while (prevI >= 0 && !ptLeaveNodes.get(prevI).isTerminalSymbol()) prevI--;
        ParseTree prevTerm = prevI >= 0 ? ptLeaveNodes.get(prevI) : null;

        int nextI = i;
        while (nextI < ptLeaveNodes.size() && !ptLeaveNodes.get(nextI).isTerminalSymbol()) nextI++;
        ParseTree nextTerm = nextI < ptLeaveNodes.size() ? ptLeaveNodes.get(nextI) : null;

        List<ATSyntaxTree> stLeaveNodes = stRoot.stream().filter(st -> st.getChildren().isEmpty()).collect(Collectors.toList());

        var matchingPrevTermOp = findMatchingLeaveNode(stLeaveNodes, prevTerm);
        var matchingNextTermOp = findMatchingLeaveNode(stLeaveNodes, nextTerm);

        int matchingPrevTermIdx = matchingPrevTermOp.map(Pair::getSecond).orElse(-1);
        int matchingNextTermIdx = matchingNextTermOp.map(Pair::getSecond).orElse(-1);

        // try to find all candidates here. if multiple check, if maybe there is a mapping in a parent scope already that adds clarity to which one to choose
        List<ATSyntaxTree> candidates = ListUtil.subListExclusive(stLeaveNodes, matchingPrevTermIdx, matchingNextTermIdx);

        if (candidates.isEmpty()) {
            System.out.println("WARNING: Did not find any mapping: " + unmappedPTElement);
        } else if (candidates.size() == 1) {
            this.add(unmappedPTElement, candidates.get(0));
        } else {
            ATSyntaxTree bestMatch = findBestMatch(candidates, unmappedPTElement);
            if (bestMatch != null) {
                this.add(unmappedPTElement, bestMatch);
            } else {
                System.out.println("WARNING: Did not find any mapping: " + unmappedPTElement);
            }
        }

//        if (matchingPrevTermIdx > -1 && matchingNextTermIdx > -1) {
//            matchingPrevTermIdx++;
//            matchingNextTermIdx--;
//            if (matchingNextTermIdx == matchingPrevTermIdx) {
//                ATSyntaxTree found = stLeaveNodes.get(matchingNextTermIdx);
//                this.add(unmappedPTElement, found);
//            }
//        } else if(matchingPrevTermIdx > -1) {
//            var mapped = findClosest(stLeaveNodes, unmappedPTElement, matchingPrevTermIdx + 1);
//            if (!mapped) {
//                ATSyntaxTree first = matchingPrevTermOp.orElseThrow().getFirst();
//                // now let's try to find some element with the same input index as in the prevMatchingTerm, and the same reachable elements as the unmappedPT from their respective parents:
//                stRoot.stream()
//                        .filter(x -> x.getParent() != null)
//                        .filter(x -> x.getParent().getReachableTerms().equals(unmappedPTElement.getParent().getReachableTerms()))
//                        .filter(x -> x.getInputIdx() < first.getInputIdx()) // find all candidates that are before
//                        .max(Comparator.comparingInt(ATSyntaxTree::getInputIdx)) // use the candidate with the highest input index
//                        .ifPresent(st -> add(unmappedPTElement, st));
//            }
//        } else if(matchingNextTermIdx > -1) {
//            var mapped = findClosest(stLeaveNodes, unmappedPTElement, matchingNextTermIdx - 1);
//        } else {
//            System.out.println("WARNING: Did not found any mapping.");
//        }
    }

    private ATSyntaxTree findBestMatch(List<ATSyntaxTree> candidates, ParseTree unmappedPTElement) {
        if (this.contains(unmappedPTElement.getParent())) {
            // try search via parent:
            ATSyntaxTree possibleParentMapping = this.getMapping(unmappedPTElement.getParent());
            var filteredCandidate = candidates.stream()
                    .filter(candidate -> candidate.getParent() == possibleParentMapping)
                    .filter(candidate -> sameType(unmappedPTElement, candidate))
                    .collect(Collectors.toList());

            if (!filteredCandidate.isEmpty()) {
                if (filteredCandidate.size() > 1) {
                    System.out.println("WARNING: Mapping is ambiguous, as more than one possible match has been found.");
                }
                return filteredCandidate.get(0);
            }

            for (ATSyntaxTree candidate : candidates) {
                if (candidate.getParent() == possibleParentMapping) return candidate;
            }
        }
        // otherwise compare reachable terms:
        for (ATSyntaxTree candidate : candidates) {
            if (candidate.getParent().getReachableTerms().equals(unmappedPTElement.getParent().getReachableTerms())) {
                return candidate;
            }
        }
        return null;
    }

//    private boolean findClosest(List<ATSyntaxTree> stLeaveNodes, ParseTree unmappedPTElement, int matchingTermIdxCandidate) {
//        Optional<ATSyntaxTree> foundOpt = ListUtil.tryGetIndex(stLeaveNodes, matchingTermIdxCandidate);
//
//        if (foundOpt.isPresent()) {
//            ATSyntaxTree found = foundOpt.get();
//            if (found.getParent().getReachableTerms().equals(unmappedPTElement.getParent().getReachableTerms())) {
//                this.add(unmappedPTElement, found);
//                return true;
//            } else {
//                System.out.println("WARNING: Did not perform mapping as reachable terms are not matching.");
//                return false;
//            }
//        } else {
//            System.out.println("WARNING: Matching Term Candidate index does not exist.");
//            return false;
//        }
//    }

    private Optional<Pair<ATSyntaxTree, Integer>> findMatchingLeaveNode(List<ATSyntaxTree> leaves, ParseTree node) {
        if (node == null) return Optional.empty();
        var matchingNextTerm = leaves.stream().filter(st -> st.getInputIdx() == node.getStartIndex()).filter(st -> st.getSymbol().getKey().equals(node.getLabel())).findFirst().orElseThrow();
        var matchingNextTermIdx = leaves.indexOf(matchingNextTerm);
        return Optional.of(Pair.of(matchingNextTerm, matchingNextTermIdx));
    }

    private List<ParseTree> findUnmappedPTElements(ParseTree parseTreeRoot) {
        List<ParseTree> nodes = new ArrayList<>();
        findUnmappedPtElementsRecursively(parseTreeRoot, nodes);
        return nodes;
    }

    private void findUnmappedPtElementsRecursively(ParseTree node, List<ParseTree> list) {
        node.getChildren().forEach(n -> findUnmappedPtElementsRecursively(n, list));
        if (!this.contains(node)) {
            list.add(node);
        }
    }

    private void add(ParseTree parseTree, ATSyntaxTree atSyntaxTree) {
        mappings.add(Pair.of(parseTree, atSyntaxTree));
    }

    public List<Pair<ParseTree, ATSyntaxTree>> getIterableMappingsCopy() {
        return new ArrayList<>(mappings);
    }

    public boolean containsMapping(ParseTree parseTree, ATSyntaxTree atSyntaxTree) {
        return mappings.contains(Pair.of(parseTree, atSyntaxTree));
    }

    public boolean contains(ParseTree parseTree) {
        return mappings.stream().map(Pair::getFirst).anyMatch(x -> x == parseTree);
    }

    public ATSyntaxTree getMapping(ParseTree parseTree) {
        return mappings.stream().filter(p -> p.getFirst() == parseTree).map(Pair::getSecond).findFirst().orElseThrow();
    }

    public List<ParseTree> getMappedParseTrees() {
        return mappings.stream().map(Pair::getFirst).distinct().collect(Collectors.toList());
    }

    public void moveMapping() {
        Map<ATSyntaxTree, List<ParseTree>> collect = mappings.stream().collect(Collectors.toMap(Pair::getSecond, x -> List.of(x.getFirst()), ListUtil::concat));

        List<ParseTree> parseTreeOrder = ptRoot.stream().collect(Collectors.toList());
        collect.forEach((atSyntaxTree, parseTrees) -> {
            List<ParseTree> modList = new ArrayList<>(parseTrees);
            if (modList.size() > 1) {
                System.out.println("INFO: More than one mapping present, trying to apply it in tree order.");
                modList.sort(Comparator.comparingInt(parseTreeOrder::indexOf));
            }

            for (ParseTree parseTree : modList) {
                atSyntaxTree.getPreSemantics().addAll(0, parseTree.getPreSemantics());
                atSyntaxTree.getSemantics().addAll(parseTree.getPostSemantics());
            }
        });
    }
}
