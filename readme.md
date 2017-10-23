geo overlay logic
Imagine a number of bounding box sets, A to D
Create a list of all the k-combinations of A to D, where k = 1 to (A to D).size (4)
each entry in the list contains the following properties:
* A list of bounding boxes (LoB)
* number of members of the list
* whether all members intersect (if number of members of the list = 1, then this is true)
* bounding box geometry of the intersection
The list of LoBs needs to be in order of the number of elements. Ascending.

For example:
<Insert example here>

Iterate through the list of list of boxes (LoB).
    If LoB.count = 1:
        then members = 1
        Do members intersect = true
        bounding box geo = the single boxes geometry
    If LoB.count = 2:
        members = 2
        calculate intersection of the LoB
        Set "do members intersect" to true of false as necessary
        Set bounding box geo to either the intersection, or null
    If LoB.count >= 3:
        members = LoB.count
        Remove the first member of the LoB. Look up the smaller LoB in the list.
        If "do members intersect" == false, then "do members intersect" for the bigger LoB = false
        If "do members intersect" == true, then calculate intersection of the smaller LoB and the first member of the bigger LoB (that we stripped earlier)
        If this intersection is false, then set "Do members intersect" to false, set the bounding box geo to null
        Otherwise, set "Do members intersect" to true and set the bounding box geo to the intersection

class ListOfBoxes {
    Geometry[] listOfBoxes;
    int numberOfMembers;
    boolean membersIntersect;
    Geometry intersection;
}
List<Geometry> geocodingResults = getGeocodes(List<String> locationEntities);
List<ListOfBoxes> EveryCombo;
EveryCombo = ConstructEveryCombo(geocodingResults); // populates EveryCombo with the k-combinations and member counts

for (lob in EveryCombo) {
    if (lob.size = 1) {
        ignore, move on;
    } else if (lob.size = 2) {
        lob.intersection = intersection(lob(0).intersection, lob(1).intersection)
        if (lob.intersection = null) {
            lob.membersIntersect = false;
        } else {
            lob.membersIntersect = true;
        }
    } else if (lob.size >= 3) {
        ListOfBoxes first = lob(0);
        List(ListOfBoxes) everythingButFirst = lob(1) .. lob(n) // n = lob.size - 1. Need to make a method for this
        if (EveryCombo.find(everythingButFirst).membersIntersect == true) {
            lob.intersection = intersection(lob(0).intersection, EveryCombo.find(everythingButFirst).intersection);
            if (lob.intersection = null) {
                lob.membersIntersect = false;
            } else {
                lob.membersIntersect = true;
            }
        } else {
            lob.membersIntersect = false;
        }
    }
}
    
