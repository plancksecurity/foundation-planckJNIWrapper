#pragma once

#include "passphrase_callback.hh"

namespace pEp {
namespace JNIAdapter {

template<typename... A> PEP_STATUS passphraseWrap(PEP_STATUS f(PEP_SESSION, A...), PEP_SESSION session, A... a) {
    pEpLog("cached passphrase mode");
    bool retryAgain = false;
    int maxRetries = 3;
    int retryCount = 0;
    PEP_STATUS status;
    do {
        // the actual target function
        pEpLog("calling passphrase_cache.api from basic_api");
        status = passphrase_cache.api(f, session, a...);
        pEpLog("PEP_STATUS:" << status);
        if (status == PEP_PASSPHRASE_REQUIRED ||
            status == PEP_WRONG_PASSPHRASE ||
            status == PEP_PASSPHRASE_FOR_NEW_KEYS_REQUIRED)
        {
            pEpLog("none of the cached passphrases worked");
            if (retryCount < maxRetries) {
                // call the app
                char *_passphrase = passphraseRequiredCallback(status);
                pEpLog("callback returned, config_passphrase() with new passphrase");
                if (status == PEP_PASSPHRASE_FOR_NEW_KEYS_REQUIRED) {
                    status = ::config_passphrase_for_new_keys(session, true, _passphrase);
                } else {
                    status = ::config_passphrase(session, passphrase_cache.add(_passphrase));
                }
                retryAgain = true;
                retryCount++;
            } else {
                pEpLog("max retries reached:" << maxRetries);
                retryAgain = false;
            }
        } else {
            retryAgain = false;
        }
    } while (retryAgain);
    return status;
}
}
}