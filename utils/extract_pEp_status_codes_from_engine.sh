################################################################################
#		 		Argument cheking			       #
################################################################################

if [ ! -f $1 ]; then
    echo -e "\e[1m\e[31mInput file not found!\e[0m"
fi

if [ -z "$2" ]; then
    echo -e "\e[1m\e[31mNo output file supplied\e[0m"
fi

if [ "$#" -ne 2 ]; then
    echo "Expected use is: $0 input_file output_file"
    exit 1
fi

################################################################################
#                                Select GNU SED                                #
################################################################################

OS="$(uname -s)"

case "${OS}" in
    Linux*)     SED=sed;;
    Darwin*)    SED=gsed;;
    CYGWIN*)    echo "UNSUPORTED YET" && exit;;
    MINGW*)     echo "UNSUPORTED YET" && exit;;
    *)          echo "UNKNOWN:${OS}" && exit;;
esac

################################################################################
#              Transform input file PEP_STATUS to yml2 status                  #
################################################################################

$SED -n '/} PEP_STATUS/q;p' $1 > $2
$SED -i -n '/STATUS_OK/,$p' $2
$SED -i -e 's/\(.*\)/\L\1/' $2
$SED -i "-e s/    pep/        pEp/g" $2
$SED -i s/=/\>/g $2
$SED -i s/,//g $2


################################################################################
#                                 Show results                                 #
################################################################################
cat $2
