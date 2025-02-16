package com.threedev.appconvertor.ui.fragments.getStarted

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.threedev.appconvertor.R
import com.threedev.appconvertor.data.models.MainViewModel
import com.threedev.appconvertor.databinding.FragmentGetStartedBinding
import com.threedev.appconvertor.helpers.SessionManager
import com.threedev.appconvertor.helpers.billing.BillingWrapper
import com.threedev.appconvertor.helpers.billing.OnBillingLibResponseListener
import com.threedev.appconvertor.ui.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class GetStartedFragment : Fragment() {

    private lateinit var binding: FragmentGetStartedBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val args: GetStartedFragmentArgs by navArgs()
    val inAppProducts = ArrayList<ProductDetails>()
    val billingWrapper: BillingWrapper by lazy {
        BillingWrapper.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingSetup()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGetStartedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext()).load(args.builderInfo.image_url).into(binding.ivAppIcon)
        binding.tvAppName.text = args.builderInfo.appName
        binding.tvPackageName.text = args.builderInfo.packageName
        binding.tvWebUrl.text = args.builderInfo.web_url

        binding.btnProceed.setOnClickListener {
            args.builderInfo.apply {
                this.pullToRefresh = binding.cbSwipeRefresh.isSelected
                this.backCompatibility = binding.cbBackComp.isSelected
                this.appExit = if (binding.cbExitComp.isSelected) {
                    if (binding.rgExit.checkedRadioButtonId == R.id.rb_double_click) {
                        "double click"
                    } else {
                        "exit dialog"
                    }
                } else ""

                val deliverTime = Calendar.getInstance().timeInMillis+86400*1000
                args.builderInfo.apply {
                    this.delivery_date = deliverTime
                    this.status = Constants.STATUS_PROCESSING_APK
                }

                val auth = FirebaseAuth.getInstance().currentUser
                Firebase.firestore.collection(auth!!.uid).document(args.builderInfo.docId).update(
                    mapOf(
                        "pullToRefresh" to args.builderInfo.pullToRefresh,
                        "backCompatibility" to args.builderInfo.backCompatibility,
                        "appExit" to args.builderInfo.appExit,
                        "delivery_date" to deliverTime,
                        "status" to Constants.STATUS_PROCESSING_APK
                    )
                ).addOnSuccessListener {
                    val index = viewModel.getAllData().value?.data?.builderData?.indexOf(args.builderInfo)
                    if (index != null) {
                        viewModel.getAllData().value?.data?.builderData?.get(index)?.apply {
                            this.pullToRefresh = args.builderInfo.pullToRefresh
                            this.backCompatibility = args.builderInfo.backCompatibility
                            this.appExit = args.builderInfo.appExit
                            this.delivery_date = args.builderInfo.delivery_date
                            this.status = Constants.STATUS_PROCESSING_APK
                        }
                    }
                    Toast.makeText(requireContext(), "Successfully Update", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }


                //billingWrapper.launchBillingFlow(arrayOf(0), requireActivity())
            }
        }

    }

    private fun billingSetup() {
        val subscriptionList = ArrayList<String>()
        val inAppList = ArrayList<String>()
        //subscriptionList.add("ITEM_PLAN_ONE_ID")
        //subscriptionList.add("ITEM_PLAN_TWO_ID")
        inAppList.add("resources.getString(R.string.remove_ad_in_app)")
        CoroutineScope(Dispatchers.IO).launch {
            billingWrapper.initialize(requireContext())
        }
        billingWrapper.subscriptionProductIds = subscriptionList
        billingWrapper.inAppProductIds = inAppList

        val libResponseListener = object : OnBillingLibResponseListener {
            override fun onPurchaseSuccess(purchase: Purchase) {

                Toast.makeText(
                    requireContext(),
                    "Product purchase successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onPurchaseError(er: String) {
                Toast.makeText(
                    requireContext(),
                    "Product purchase error. Please try again",
                    Toast.LENGTH_SHORT
                ).show()

            }

            override fun onSubsOfferDetails(subscriptionOfferDetails: List<ProductDetails>) {
                Log.d(
                    "purchaseTAG",
                    "onSubsOfferDetails: list size: " + subscriptionOfferDetails.size
                );
                if (subscriptionOfferDetails.isNotEmpty() && subscriptionOfferDetails[0].subscriptionOfferDetails!!.isNotEmpty()) {
                    val priceMonth =
                        subscriptionOfferDetails[0].subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[0].formattedPrice
                    //binding.tvSilverPrice.setText(priceMonth);
                    val priceYear =
                        subscriptionOfferDetails[1].subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[0].formattedPrice
                    //binding.tvGoldPrice.setText(priceYear);
                }
            }

            override fun onInAppOfferDetails(inAppOfferDetails: List<ProductDetails>) {
                Log.d("purchaseTAG", "onInAppOfferDetails: list size: " + inAppOfferDetails.size);
                if (inAppOfferDetails.isNotEmpty()) {
                    inAppProducts.clear()
                    inAppProducts.addAll(inAppOfferDetails)
                }
            }
        }
        try {
            billingWrapper.setBillingListener(libResponseListener);
            billingWrapper.queryOffers();
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}