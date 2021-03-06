// SPDX-FileCopyrightText: 2022 Synacor, Inc.
// SPDX-FileCopyrightText: 2022 Zextras <https://www.zextras.com>
//
// SPDX-License-Identifier: GPL-2.0-only

package com.zimbra.clam;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.service.mail.UploadScanner;

public class ClamScannerExt implements ZimbraExtension {

    private static final Log LOG = ZimbraLog.extensions;
   
    public ClamScannerExt() {
    }

    @Override
    public synchronized void init() {
        
        try {
            mConfig = new ClamScannerConfig();
           
            if (!mConfig.getEnabled()) {
                LOG.info("attachment scan is disabled");
                return;
            }
            
            String[] urls = mConfig.getURL();
            for (int i = 0; i < urls.length; i++) {
                ClamScanner clamScanner = new ClamScanner();
                String url = urls[i];
                clamScanner.setURL(url);
                UploadScanner.registerScanner(clamScanner);
                clamScannerList.add(clamScanner);
            }
        } catch (ServiceException e) {
            LOG.error("error creating scanner", e);
        } catch (MalformedURLException e) {
            LOG.error("error creating scanner", e);
        }
    }

    @Override
    public void destroy() {
        for (Iterator iter = clamScannerList.iterator(); iter.hasNext();) {
            ClamScanner clamScanner = (ClamScanner)iter.next();
            UploadScanner.unregisterScanner(clamScanner);
        }
    }

    private ClamScannerConfig mConfig;
    private List<ClamScanner> clamScannerList = new LinkedList<ClamScanner>();  

    @Override
    public String getName() {
        return "clamscanner";
    }
}
