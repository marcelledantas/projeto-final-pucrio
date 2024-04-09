package pn.Model;

import java.io.Serializable;

public class Group {
        protected int groupType;
        protected int groupID;

        public Group(int groupType, int groupID) {
            this.groupType = groupType;
            this.groupID = groupID;
        }

        public int compareTo(Group otherGroup) {
            if (this.groupType < otherGroup.groupType) {
                return -1;
            } else if (this.groupType > otherGroup.groupType) {
                return 1;
            } else if (this.groupID < otherGroup.groupID) {
                return -1;
            } else {
                return this.groupID > otherGroup.groupID ? 1 : 0;
            }
        }

        public int getGroupType() {
            return this.groupType;
        }

        public void setGroupType(int groupType) {
            this.groupType = groupType;
        }

        public int getGroupID() {
            return this.groupID;
        }

        public void setGroupID(int groupID) {
            this.groupID = groupID;
        }

        public int hashCode() {
            return this.groupID;
        }

        public boolean equals(Object otherGroup) {
            Group other = (Group)otherGroup;
            return this.getGroupID() == other.getGroupID() && this.getGroupType() == other.getGroupType();
        }

        public String toString() {
            return "Group[id=" + this.getGroupID() + "/type=" + this.getGroupType() + "]";
        }
}
