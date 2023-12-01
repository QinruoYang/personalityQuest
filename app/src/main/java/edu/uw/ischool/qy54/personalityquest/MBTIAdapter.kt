package edu.uw.ischool.qy54.personalityquest
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MBTIAdapter(private var mbtiList: List<UserResult>, private val onClick: (UserResult) -> Unit) : RecyclerView.Adapter<MBTIAdapter.MBTIViewHolder>() {

    class MBTIViewHolder(view: View, val onClick: (Int) -> Unit) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.mbtiItemTextView)

        fun bind(userResult: UserResult) {
            textView.text = "${userResult.name}: ${userResult.mbtiType}"
            itemView.setOnClickListener { onClick(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MBTIViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mbti_item, parent, false)
        return MBTIViewHolder(view) { position ->
            val userResult = mbtiList[position]
            onClick(userResult)
        }
    }

    override fun onBindViewHolder(holder: MBTIViewHolder, position: Int) {
        val mbtiType = mbtiList[position]
        holder.bind(mbtiType)
    }

    override fun getItemCount() = mbtiList.size

    fun updateData(newData: List<UserResult>) {
        mbtiList = newData
        notifyDataSetChanged()
    }
}
