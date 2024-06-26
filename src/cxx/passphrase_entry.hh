#pragma once

#include <string>

namespace pEp {
    namespace JNIAdapter {
        struct passphrase_entry {
            static const size_t max_len = static_cast<const size_t>(250 * 4);
            passphrase_entry(const std::string email, const std::string passphrase);

            std::string email;
            std::string passphrase;

            bool operator==(const passphrase_entry& other) const {
                return email == other.email && passphrase == other.passphrase;
            }
        };
    }
}
