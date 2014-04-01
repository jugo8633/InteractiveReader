package interactive.bookshelfuser;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import interactive.billing.*;
import interactive.common.Logs;

public class BillingHandler
{

	// Public key
	private final String	base64EncodedPublicKey	= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnKjKhkMTdh7rAloCNXoW+cNTZA2MMCOcQG0JzNp9UV/Vpn2pGwHzoP9xoFNw4xe5g75ARh9dQ73IzhmtFhnb6xoUyJxk7DUq5Rbs9q60lALnzy6FqdpIXKfAhOGk472PQXLmuBfTb5uNNmbF9vZ1U8bi0OnXL7UOHckyMaPy92WqZXjvzNK8H3sfQz/xeu0c/9NZ52HkmjacPTslzY+odemTlvKS//QXLbqglzdUa3YrgUrjAbBb1J8h+a/1u1Bn6gxp3yW3MKWGr/4FdPCY/YGkRioATil9c0vAeRiMG0l0KjOvx9/9xglZQjgGJFeYCBruU1gOQj//zbcnBGHwnwIDAQAB";

	// for our products
	private final String	SKU_BOOK				= "android.test.purchased";
	//	private final String	SKU_BOOK				= "book";

	// (arbitrary) request code for the purchase flow
	private final int		RC_REQUEST				= 10001;

	// The helper object
	private IabHelper		mHelper					= null;

	// Debug
	private final boolean	IS_DEBUG				= true;

	private Context			theContext				= null;

	public BillingHandler(Context context)
	{
		super();

		mHelper = new IabHelper(context, base64EncodedPublicKey);
		mHelper.enableDebugLogging(IS_DEBUG);
		startSetup(context);
		theContext = context;
	}

	@Override
	protected void finalize() throws Throwable
	{
		closeService();
		super.finalize();
	}

	public void closeService()
	{
		Logs.showTrace("Destroying billing helper.");
		if (mHelper != null)
		{
			mHelper.dispose();
			mHelper = null;
		}
	}

	private void startSetup(final Context context)
	{
		Logs.showTrace("Billing start setup");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
		{
			@Override
			public void onIabSetupFinished(IabResult result)
			{
				Logs.showTrace("Billing setup finish.");

				if (!result.isSuccess())
				{
					Logs.complain(context, "Problem setting up in-app billing: " + result);
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				// IAB is fully set up. Now, let's get an inventory of stuff we own.
				Logs.showTrace("Setup successful. Querying inventory.");
				//mHelper.queryInventoryAsync(mQueryFinishedListener);

				List<String> additionalSkuList = new ArrayList<String>();
				additionalSkuList.add(SKU_BOOK);

				// 查詢可購買的商品
				//mHelper.queryInventoryAsync(true, additionalSkuList, mQueryFinishedListener);

				// 查詢已購買產品
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}

	// Listener that's called when we finish querying the items and subscriptions we own
	IabHelper.QueryInventoryFinishedListener	mGotInventoryListener		= new IabHelper.QueryInventoryFinishedListener()
																			{
																				public void onQueryInventoryFinished(
																						IabResult result,
																						Inventory inventory)
																				{

																					if (result.isFailure())
																					{
																						// handle error here
																						Logs.showTrace("Got inventory fail result="
																								+ result.getMessage());
																					}
																					else
																					{

																						Purchase bookPurchase = inventory
																								.getPurchase(SKU_BOOK);

																						if (bookPurchase != null
																								&& verifyDeveloperPayload(bookPurchase))
																						{
																							Logs.showTrace("We had purchased this product");
																							mHelper.consumeAsync(
																									inventory
																											.getPurchase(SKU_BOOK),
																									mConsumeFinishedListener);
																							return;
																						}

																					}
																				}
																			};

	IabHelper.QueryInventoryFinishedListener	mQueryFinishedListener		= new IabHelper.QueryInventoryFinishedListener()
																			{
																				@Override
																				public void onQueryInventoryFinished(
																						IabResult result,
																						Inventory inventory)
																				{
																					Logs.showTrace("Query inventory finished");

																					// Have we been disposed of in the meantime? If so, quit.
																					if (mHelper == null)
																						return;

																					// Is it a failure?
																					if (result.isFailure())
																					{
																						Logs.showTrace("Failed to query inventory: "
																								+ result);
																						return;
																					}

																					Logs.showTrace("Query inventory was successful.");

																					SkuDetails skuDetail = inventory
																							.getSkuDetails(SKU_BOOK);
																					if (null != skuDetail)
																					{
																						Logs.showTrace("get SKU detail="
																								+ skuDetail.getSku()
																								+ " price="
																								+ skuDetail.getPrice());
																					}

																				}

																			};

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener			mConsumeFinishedListener	= new IabHelper.OnConsumeFinishedListener()
																			{
																				public void onConsumeFinished(
																						Purchase purchase,
																						IabResult result)
																				{
																					Logs.showTrace("Consumption finished. Purchase: "
																							+ purchase
																							+ ", result: "
																							+ result);

																					// if we were disposed of in the meantime, quit.
																					if (mHelper == null)
																						return;

																					if (result.isSuccess())
																					{
																						// successfully consumed
																						Logs.showTrace("Consumption successful. Provisioning.");

																					}
																					else
																					{
																						Logs.showTrace("Error while consuming: "
																								+ result);
																					}

																					Logs.showTrace("End consumption flow.");
																				}
																			};

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p)
	{
		String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 * 
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */

		return true;
	}

	public void launchPurchase(Activity activity, String strSKU)
	{
		String payload = "";
		mHelper.launchPurchaseFlow(activity, SKU_BOOK, RC_REQUEST, mPurchaseFinishedListener, payload);
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener	mPurchaseFinishedListener	= new IabHelper.OnIabPurchaseFinishedListener()
																		{
																			public void onIabPurchaseFinished(
																					IabResult result, Purchase purchase)
																			{
																				Logs.showTrace("Purchase finished: "
																						+ result + ", purchase: "
																						+ purchase);

																				// if we were disposed of in the meantime, quit.
																				if (mHelper == null)
																					return;

																				if (result.isFailure())
																				{
																					Logs.complain(theContext,
																							"Error purchasing: "
																									+ result);
																					return;
																				}
																				if (!verifyDeveloperPayload(purchase))
																				{
																					Logs.complain(theContext,
																							"Error purchasing. Authenticity verification failed.");
																					return;
																				}

																				Logs.showTrace("Purchase successful.");

																				if (purchase.getSku().equals(SKU_BOOK))
																				{
																					mHelper.consumeAsync(purchase,
																							mConsumeFinishedListener);
																					Logs.showTrace("Billing purchase success SKU="
																							+ SKU_BOOK);
																				}

																			}
																		};

	public boolean handleActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (mHelper == null)
		{
			return false;
		}

		return mHelper.handleActivityResult(requestCode, resultCode, data);
	}
}
