################################################################################
#		 		Argument cheking			       #
################################################################################

if [ ! -f $1 ]; then
    echo -e "\e[1m\e[31mInput file not found!\e[0m"
fi

if [ "$#" -ne 1 ]; then
    echo "Expected use is: $0 input_file"
    exit 1
fi

STAT_FILE=status_list.yml2
PASS_FILE=passphrase_status_list.yml2

################################################################################
#                                Select GNU SED                                #
################################################################################

OS="$(uname -s)"

case "${OS}" in
    Linux*)     SED=sed;;
    Darwin*)    SED=gsed;;
    MSYS*)      SED=sed;;
    CYGWIN*)    echo "UNSUPORTED YET" && exit;;
    MINGW*)     echo "UNSUPORTED YET" && exit;;
    *)          echo "UNKNOWN:${OS}" && exit;;
esac

################################################################################
#              Transform input file PEP_STATUS to yml2 status                  #
################################################################################

$SED -n '/} PEP_STATUS/q;p' $1 > $STAT_FILE
$SED -i -n '/STATUS_OK/,$p' $STAT_FILE
$SED -i -e 's/\(.*\)/\L\1/' $STAT_FILE
$SED -i "-e s/    pep/        pEp/g" $STAT_FILE
$SED -i s/=/\>/g $STAT_FILE
$SED -i s/,//g $STAT_FILE

grep -i passphrase $STAT_FILE | sed -e 's/^/enumitem/' > $PASS_FILE

################################################################################
#                                 Show results                                 #
################################################################################
cat $STAT_FILE
cat $PASS_FILE
