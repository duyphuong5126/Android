package com.huy.monthlyfinance.ProcessData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.huy.monthlyfinance.MainApplication;
import com.huy.monthlyfinance.Model.ProductDetail;
import com.huy.monthlyfinance.R;

import java.util.*;

/**
 * Created by huy nguyen on 12/24/2016.
 */
public class Apriori {
    private Set<Tuple> c;
    private Set<Tuple> l;
    private int d[][];
    private double minSupport;
    private double minConf;
    private Set<Integer> candidate_set_one;

    public boolean isInitialized() {
        return isInitialized;
    }

    private static boolean isInitialized;

    private static Apriori mInstance;

    public static Apriori getInstance(double minSupport, double minConf) {
        if (mInstance == null) {
            mInstance = new Apriori();
        }
        if (mInstance.c == null) {
            mInstance.c = new HashSet<>();
        }
        mInstance.c.clear();
        if (mInstance.l == null) {
            mInstance.l = new HashSet<>();
        }
        mInstance.l.clear();
        mInstance.minConf = minConf;
        mInstance.minSupport = minSupport;
        isInitialized = false;
        return mInstance;
    }

    private Apriori() {
        candidate_set_one = new HashSet<>();
    }

    public void execute() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mListener.onBegin();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                if (isInitialized) {
                    int i, j;
//		Set<Integer> candidate_set = new HashSet<>();
                    for (i = 0; i < d.length; i++) {
                        System.out.println("Transaction Number: " + (i + 1) + ":");
                        for (j = 0; j < d[i].length; j++) {
                            System.out.print("Item number " + (j + 1) + " = ");
                            System.out.println(d[i][j]);
                            candidate_set_one.add(d[i][j]);
                        }
                    }

                    for (int aCandidate_set_one : candidate_set_one) {
                        Set<Integer> s = new HashSet<>();
                        s.add(aCandidate_set_one);
                        Tuple t = new Tuple(s, countSupport(s));
                        System.out.println("do ho tro la:" + t.support);
                        c.add(t);
                    }

                    prune();
                    generateFrequentItemSets();
                    isInitialized = false;
                    return true;
                } else {
                    mInstance.mListener.onError("You must call .initialize(...).execute(...), not .execute(...)");
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if (success) {
                    mListener.onSuccess();
                }
            }
        }.execute();
    }

    private AprioriListener mListener;

    public interface AprioriListener {
        void onBegin();
        void onSuccess();
        void onError(String message);
    }

    public Apriori setExecuteListener(AprioriListener listener) {
        mInstance.mListener = listener;
        return mInstance;
    }

    private float countSupport(Set<Integer> s) {
        int i, k;
        int support = 0;
        int count;
        int sumCount = d.length;
        boolean containsElement;
        for (i = 0; i < d.length; i++) {
            count = 0;
            for (Integer element : s) {
                containsElement = false;
                for (k = 0; k < d[i].length; k++) {
                    if (element == d[i][k]) {
                        containsElement = true;
                        count++;
                        break;
                    }
                }
                if (!containsElement) {
                    break;
                }
            }
            if (count == s.size()) {
                support++;
            }
        }
        return (support / (float) sumCount);
    }

    private void prune() {
        l.clear();
        System.out.println("-+- L -+-");
        for (Tuple t : c) {
            if (t.support >= minSupport) {
                l.add(t);
                System.out.println(t.itemSet + " : " + t.support);
            }
        }
    }

    private void generateFrequentItemSets() {
        boolean toBeContinued = true;
        int element;
        int size = 1;
        Set<Set> candidate_set = new HashSet<>();
        while (toBeContinued) {
            candidate_set.clear();
            c.clear();
            for (Tuple t1 : l) {
                Set<Integer> temp = t1.itemSet;
                for (Tuple t2 : l) {
                    for (Integer anItemSet : t2.itemSet) {
                        try {
                            element = anItemSet;
                        } catch (ConcurrentModificationException e) {
                            // Sometimes this Exception gets thrown, so simply break in that case.
                            break;
                        }
                        temp.add(element);
                        if (temp.size() != size) {
                            Integer[] int_arr = temp.toArray(new Integer[0]);
                            Set<Integer> temp2 = new HashSet<>();
                            Collections.addAll(temp2, int_arr);
                            candidate_set.add(temp2);
                            temp.remove(element);
                        }
                    }
                }
            }
            for (Set s : candidate_set) {
                // These lines cause warnings, as the candidate_set Set stores a raw set.
                c.add(new Tuple(s, countSupport(s)));
            }
            prune();
            if(l.size() <= 1) {
                toBeContinued = false;
            }
            size++;
        }
        System.out.println("\n=+= FINAL LIST =+=");
        Set<Integer> demo;
        for (Tuple t : l) {
            System.out.println(t.itemSet + " : " + t.support);
            System.out.println("truoc khi goi ham:");
            for (int productId : t.itemSet) {
                demo = generateFrequentAfterCF(productId);
                System.out.println("sau khi goi ham:");
                System.out.println("ket qua sau khi goi ham:" + demo);
            }
        }
//			for(Tuple rs : demo){
//				System.out.println("ket qua can la::"+rs.itemSet);
//			}
//			Iterator<Integer> iterator = t.itemSet.iterator();
//			while(iterator.hasNext()) {
//				System.out.println("la: "+iterator.next());
//				Set<Integer> s = new HashSet<>();
//				s.add(iterator.next());
//				 Tuple h = new Tuple(s, countSupport(s));
//				System.out.println("do ho tro la:"+t.support);
//				c.add(t);

//				Iterator<Integer> iterator_one = candidate_set_one.iterator();
//				Iterator<Integer> iterator = t.itemSet.iterator();
//			
//				while(iterator.hasNext()) {
////					System.out.println("la: "+iterator.next());
//					Set<Integer> s = new HashSet<>();
//					s.add(iterator.next());
//					Tuple h = new Tuple(s, countSupport(s));
//					System.out.println("do ho tro la:"+h.support);
//					if(((t.support)/(float)(h.support))>minConf){
//						System.out.println("luat la:"+h.itemSet+"-->"+s.iterator().hasNext());
//						
//					}
//					
//				}
//			}
//			for(int h=0;h<t.itemSet.size();h++){
//				System.out.println(t.itemSet[h]);
////				if(((t.support)/t.support))
////				while(t.itemSet.iterator().hasNext())
////				{
////					System.out.println(t.support);
////				}
//			}
    }
//		generateFrequentAterCF();

    // function generate to Frequent
    public Set<Integer> generateFrequentAfterCF(int productId) {
        Set<Integer> result = new HashSet<>();
        for (Tuple t : l) {
            //System.out.println(t.itemSet + " : " + t.support);
            for (Integer anItemSet : t.itemSet) {
//					System.out.println("la: "+iterator.next());
                Set<Integer> s = new HashSet<>();
                s.add(anItemSet);
                Tuple h = new Tuple(s, countSupport(s));
                //System.out.println("do ho tro la:" + h.support);
                if (((t.support / h.support) > minConf) && (h.itemSet.contains(productId))) {
                    System.out.println("luat la:" + h.itemSet + "-->" + s.iterator().hasNext());
//						int v=h.itemSet.hashCode();
                    for (int i : t.itemSet) {
                        if (i != h.itemSet.hashCode()) {
                            result.add(i);
                        }
                    }
                }
            }
        }
        return result;
    }

    public Apriori initialize(List<ProductDetail> productDetails) {
        Context context = MainApplication.getInstance().getApplicationContext();
        Resources resources = context.getResources();
        if (productDetails == null) {
            mListener.onError(resources.getString(R.string.error_empty_bought_products));
            return null;
        }
        if (productDetails.isEmpty()) {
            mListener.onError(resources.getString(R.string.error_empty_bought_products));
            return null;
        }
        @SuppressLint("UseSparseArrays")
        Map<Integer, List<Integer>> m = new HashMap<>();
        List<Integer> temp;
        for (ProductDetail productDetail : productDetails) {
            int list_no = Integer.parseInt(productDetail.getTransactionID());
            int object = Integer.parseInt(productDetail.getProductID());
            temp = m.get(list_no);
            if (temp == null) {
                temp = new LinkedList<>();
                m.put(list_no, temp);
            }
            temp.add(object);
            m.put(list_no, temp);
        }
//		Map<Integer, List <Integer>> m = new HashMap<>();
        /*Map<Integer, List <Integer>> m = new HashMap<>();
        List<Integer> temp=new LinkedList<>();*/
        Set<Integer> keySet = m.keySet();
        mInstance.d = new int[keySet.size()][];
        Iterator<Integer> iterator = keySet.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            temp = m.get(iterator.next());
            Integer[] int_arr = temp.toArray(new Integer[0]);
            mInstance.d[count] = new int[int_arr.length];
            for (int i = 0; i < d[count].length; i++) {
                mInstance.d[count][i] = int_arr[i];
            }
            count++;
        }
        isInitialized = true;
        return mInstance;
    }
}
