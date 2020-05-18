const functions = require('firebase-functions');
const admin =  require('firebase-admin');

admin.initializeApp();

exports.addAdminRole = functions.https.onCall((email,context)=>{

    return admin.auth().getUserByEmail(email).then(user =>{
        return admin.auth().setCustomUserClaims(user.uid,{admin: true});
    }).then(()=>{
        return {message: `Success ${email} has been made an admin`}
    }).catch(err => {
        return err;
    });
});

exports.deleteUser = functions.https.onCall((data,context)=>{
    admin.auth().deleteUser(data.uid)
    .then(function() {
      console.log('Successfully deleted user');
    })
    .catch(function(error) {
      console.log('Error deleting user:', error,data.uid);
    });
});


