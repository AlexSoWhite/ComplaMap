package com.example.complamap.model

import com.orhanobut.hawk.Hawk

object ComplaintManager {

        private var complaint: Complaint? = null
        init {
            complaint = getComplaintFromCache()
        }

        fun getCurrentComplaint(): Complaint? {
            return complaint
        }

        private fun getComplaintFromCache(): Complaint? {
            if (Hawk.isBuilt()) {
                return Hawk.get("complaint", null)
            }
            return null
        }

        fun setComplaint(complaint: Complaint?) {
            this.complaint = complaint
            // кладем новую жалобу в кэш
        }

        fun deleteComplaintFromCache() {
            if (Hawk.isBuilt()) {
                Hawk.delete("complaint")
            }
        }
    }

